package com.lavender;


import com.lavender.channel.handler.ErpcRequestDecoder;
import com.lavender.channel.handler.ErpcResponseEncoder;
import com.lavender.channel.handler.MethodCallHandler;
import com.lavender.config.Configuration;
import com.lavender.core.ErpcShutdownHook;
import com.lavender.core.HeartbeatDetector;
import com.lavender.discovery.RegistryConfig;
import com.lavender.annotation.ErpcImpl;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.transport.message.ErpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:13
 **/
@Slf4j
@Data
public class ErpcBootStrap {


    private Configuration configuration;
    private static ErpcBootStrap erpcBootStrap = new ErpcBootStrap();
    public static final ThreadLocal<ErpcRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();




    public static final Map<String, ServiceConfig<?>> SERVICES_LIST = new HashMap<>(16);

    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);
    public static final TreeMap<Long, Channel> ANSWER_TIME_CHANNEL_CACHE = new TreeMap<>();

    public final static Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(8);




    private ErpcBootStrap(){
        configuration = new Configuration();
    }

    public static ErpcBootStrap getInstance() {
        return erpcBootStrap;
    }

    /**
     * define name of temparary appName
     * @param appName
     * @return
     */
    public ErpcBootStrap application(String appName) {
        this.configuration.setApplicationName(appName);
        return this;
    }

    /**
     * config a registry center
     * @param registryConfig
     * @return
     */
    public ErpcBootStrap registry(RegistryConfig registryConfig) {
        configuration.setRegistryConfig(registryConfig);
        return this;
    }
    public ErpcBootStrap loadBalancer(LoadBalancer loadBalancer) {
        configuration.setLoadBalancer(loadBalancer);
        return this;
    }


    /**
     * publish a service
     * @param service
     * @return
     */
    public ErpcBootStrap publish(ServiceConfig<?> service) {
        this.configuration.getRegistryConfig().getRegistry().register(service);
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
        // register a hook
        Runtime.getRuntime().addShutdownHook(new ErpcShutdownHook());

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
            ChannelFuture channelFuture = serverBootstrap.bind(configuration.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ErpcBootStrap reference(ReferenceConfig<?> reference) {
        HeartbeatDetector.detectHeartbeat(reference.getInterface().getName());
        reference.setRegistry(configuration.getRegistryConfig().getRegistry());
        reference.setGroup(this.getConfiguration().getGroup());

        return this;
    }

    public ErpcBootStrap serialize(String type) {
        configuration.setSerializeType(type);
        if(log.isDebugEnabled()){
            log.debug("使用的序列化方式为【{}】", type);
        }
        return this;
    }
    public ErpcBootStrap compress(String type) {
        configuration.setCompressType(type);
        if(log.isDebugEnabled()){
            log.debug("使用的序列化方式为【{}】", type);
        }
        return this;
    }
    public ErpcBootStrap scan(String packageName){
        List<String> classNames = getAllClassNames(packageName);
        List<Class<?>> clazzs =classNames.stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(clazz -> clazz.getAnnotation(ErpcImpl.class) != null)
                .collect(Collectors.toList());
        for (Class<?> clazz : clazzs) {
            Class<?>[] interfaces = clazz.getInterfaces();
            Object instance = null;
            try {
                instance = clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            ErpcImpl erpcImpl = clazz.getAnnotation(ErpcImpl.class);
            String group = erpcImpl.group();

            for (Class<?> anInterface : interfaces) {
                ServiceConfig<?> serviceConfig = new ServiceConfig<>();
                serviceConfig.setInterface(anInterface);
                serviceConfig.setRef(instance);
                serviceConfig.setGroup(group);
                publish(serviceConfig);
                if(log.isDebugEnabled()){
                    log.debug("扫描到服务【{}】", anInterface);

                }
                System.out.println("扫描到服务"+anInterface);
            }


        }


        return this;

    }

    private List<String> getAllClassNames(String packageName) {
        String basePath = packageName.replaceAll("\\.", "/");
        URL resource = ClassLoader.getSystemClassLoader().getResource(basePath);
        if(resource == null){
            throw new RuntimeException("包扫描发现路径不存在");
        }
        String packagePath = resource.getPath();
        packagePath = packagePath.replaceFirst("/", "");

        try (Stream<Path> paths = Files.walk(Paths.get(packagePath))) {
            List<String> classNames = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.toLowerCase().endsWith(".class"))
                    .map(classPath -> {
                        String className = classPath.replaceAll("\\\\", ".");
                        className = className.replaceAll(".class", "");
                        className = className.substring(className.indexOf(packageName));
                        return className;
                    })
                    .toList();
            return classNames;
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    public ErpcBootStrap group(String group) {
        this.getConfiguration().setGroup(group);
        return this;
    }
}
