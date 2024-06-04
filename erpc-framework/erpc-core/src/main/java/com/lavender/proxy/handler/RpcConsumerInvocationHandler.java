package com.lavender.proxy.handler;

import com.lavender.Configuration;
import com.lavender.ErpcBootStrap;
import com.lavender.compress.CompressorFactory;
import com.lavender.discovery.NettyBootstrapInitializer;
import com.lavender.discovery.Registry;
import com.lavender.exceptions.DiscoverRegistryException;
import com.lavender.exceptions.NetworkException;
import com.lavender.serialiize.SerializerFactory;
import com.lavender.transport.enumeration.RequestType;
import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.ErpcRequestPayload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-31 20:45
 **/

@Slf4j
public class RpcConsumerInvocationHandler implements InvocationHandler {
    private Registry registry;
    private Class<?> interfaceReceiver;

    public RpcConsumerInvocationHandler(Registry registry, Class<?> interfaceReceiver) {
        this.registry = registry;
        this.interfaceReceiver = interfaceReceiver;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Class<?> getInterfaceReceiver() {
        return interfaceReceiver;
    }

    public void setInterfaceReceiver(Class<?> interfaceReceiver) {
        this.interfaceReceiver = interfaceReceiver;
    }
    private Channel getAvailableChannel(InetSocketAddress address){
        Channel channel = ErpcBootStrap.CHANNEL_CACHE.get(address);
        if(channel == null){
//                    channel = NettyBootstrapInitializer.getBootstrap().connect(address).await().channel();
            CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
            NettyBootstrapInitializer.getBootstrap().connect(address).addListener( (ChannelFutureListener) promise -> {
                if(promise.isDone()){
                    if(log.isDebugEnabled()){
                        log.debug("已经和【{}】成功建立了连接", address);
                    }
                    channelFuture.complete(promise.channel());
                }
                else if(!promise.isSuccess()){
                    channelFuture.completeExceptionally(promise.cause());
                }
            });
            try {
                channel = channelFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("获取通道发生异常.", e);
                // todo 添加异常
                throw new DiscoverRegistryException();
            }
            ErpcBootStrap.CHANNEL_CACHE.put(address, channel);
        }
        if(channel == null){
            log.error("创建与【{}】通道时发生异常！", address);
            throw new NetworkException("创建通道时发生异常！");
        }
        return channel;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ErpcRequestPayload requestPayload = ErpcRequestPayload.builder()
                .interfaceName(interfaceReceiver.getName())
                .methodName(method.getName())
                .parametersType(method.getParameterTypes())
                .parametersValue(args)
                .returnType(method.getReturnType())
                .build();
        Configuration configuration = ErpcBootStrap.getInstance().getConfiguration();
        ErpcRequest erpcRequest = ErpcRequest.builder()
                .requestId(configuration.getIdGenerator().getId())
                .compressType(CompressorFactory.getCompressorWraper(configuration.getCompressType()).getCode())
                .requestType(RequestType.REQUEST.getId())
                .serializeType(SerializerFactory.getSerializerWraper(configuration.getSerializeType()).getCode())
                .timeStamp(new Date().getTime())
                .requestPayload(requestPayload)
                .build();
        ErpcBootStrap.REQUEST_THREAD_LOCAL.set(erpcRequest);

        InetSocketAddress address = configuration.getLoadBalancer().selectServiceAddress(interfaceReceiver.getName());
        if(log.isDebugEnabled()){
            log.debug("服务调用方， 发现了服务【{}】的可用主机【{}】。", interfaceReceiver.getName(), address);
        }
        Channel channel = getAvailableChannel(address);
        if(log.isDebugEnabled()){
            log.debug("获取了和【{}】建立的连接通道", address, interfaceReceiver.getName());
        }

        ErpcBootStrap.REQUEST_THREAD_LOCAL.set(erpcRequest);
        // use netty to send rpc request

        //sync
//                ChannelFuture channelFuture = channel.writeAndFlush(new Object());
//                if(channelFuture.isDone()){
//                    Object object = channelFuture.getNow();
//                }
//                else if(!channelFuture.isSuccess()){
//                    Throwable cause = channelFuture.cause();
//                    throw new RuntimeException(cause);
//                }
        //async
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        ErpcBootStrap.PENDING_REQUEST.put(erpcRequest.getRequestId(), completableFuture);
        channel.writeAndFlush(erpcRequest).addListener(promise ->{
            if(!promise.isSuccess()){
                completableFuture.completeExceptionally(promise.cause());
            }
        });
        ErpcBootStrap.REQUEST_THREAD_LOCAL.remove();

        return completableFuture.get(10, TimeUnit.SECONDS);
    }

}
