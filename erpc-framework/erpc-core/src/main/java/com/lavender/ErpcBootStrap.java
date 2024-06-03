package com.lavender;


import com.lavender.channel.handler.ErpcRequestDecoder;
import com.lavender.channel.handler.ErpcResponseEncoder;
import com.lavender.channel.handler.MethodCallHandler;
import com.lavender.core.HeartbeatDetector;
import com.lavender.discovery.Registry;
import com.lavender.discovery.RegistryConfig;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.loadbalancer.impl.ConsistentHashLoadBalancer;
import com.lavender.loadbalancer.impl.MinResponseTimeLoadBalancer;
import com.lavender.loadbalancer.impl.RoundRobinLoadBalancer;
import com.lavender.serialiize.impl.JdkSerializer;
import com.lavender.transport.message.ErpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:13
 **/
@Slf4j
public class ErpcBootStrap {

    public static final int PORT = 8098;
    private static ErpcBootStrap erpcBootStrap = new ErpcBootStrap();
    public static final ThreadLocal<ErpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();
    private String applicationName = "default";

    private ProtocolConfig protocolConfig;

    public static final IDGenerator ID_GENERATOR = new IDGenerator(1, 1);
    private Registry registry;
    public static LoadBalancer LOAD_BALANCER;
    public static final Map<String, ServiceConfig<?>> SERVICES_LIST = new HashMap<>(16);

    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);
    public static final TreeMap<Long, Channel> ANSWER_TIME_CHANNEL_CACHE = new TreeMap<>();

    public final static Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(8);


    public static String SERIALIZE_TYPE = "jdk";
    public static String COMPRESS_TYPE = "gzip";


    private ErpcBootStrap(){


    }

    public static ErpcBootStrap getInstance() {
        return erpcBootStrap;
    }
    public Registry getRegistry() {
        return registry;
    }

    /**
     * define name of temparary appName
     * @param appName
     * @return
     */
    public ErpcBootStrap application(String appName) {
        this.applicationName = appName;
        return this;
    }

    /**
     * config a registry center
     * @param registryConfig
     * @return
     */
    public ErpcBootStrap registry(RegistryConfig registryConfig) {
        this.registry = registryConfig.getRegistry();
        // todo 需要修改
        ErpcBootStrap.LOAD_BALANCER = new MinResponseTimeLoadBalancer();
        return this;
    }

    /**
     * select a sequecelize protocol
     * @param protocolConfig
     * @return
     */
    public ErpcBootStrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if(log.isDebugEnabled()){
            log.debug("当前工程使用了，{}协议进行序列化", protocolConfig.toString());
        }
        return this;
    }

    /**
     * publish a service
     * @param service
     * @return
     */
    public ErpcBootStrap publish(ServiceConfig<?> service) {
        registry.register(service);
        SERVICES_LIST.put(service.getInterface().getName(), service);
        return this;
    }

    public ErpcBootStrap publish(List<ServiceConfig<?>> services) {
        for(ServiceConfig<?> service : services){
            this.publish(service);
        }
        return this;
    }


    /**
     * start netty service
     */
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap = serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new LoggingHandler())
                            .addLast(new ErpcRequestDecoder())
                            .addLast(new MethodCallHandler())
                            .addLast(new ErpcResponseEncoder());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ErpcBootStrap reference(ReferenceConfig<?> reference) {
        HeartbeatDetector.detectHeartbeat(reference.getInterface().getName());
        reference.setRegistry(registry);

        return this;
    }

    public ErpcBootStrap serialize(String type) {
        SERIALIZE_TYPE = type;
        if(log.isDebugEnabled()){
            log.debug("使用的序列化方式为【{}】", type);
        }
        return this;
    }
    public ErpcBootStrap compress(String type) {
        COMPRESS_TYPE = type;
        if(log.isDebugEnabled()){
            log.debug("使用的序列化方式为【{}】", type);
        }
        return this;
    }
}
