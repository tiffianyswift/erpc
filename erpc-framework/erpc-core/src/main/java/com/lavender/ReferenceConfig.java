package com.lavender;

import com.lavender.discovery.NettyBootstrapInitializer;
import com.lavender.discovery.Registry;
import com.lavender.discovery.RegistryConfig;
import com.lavender.exceptions.NetworkException;
import com.lavender.proxy.handler.RpcConsumerInvocationHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:52
 **/
@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceReceiver;
    private Registry registry;




    public T get(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T>[] classes = new Class[]{interfaceReceiver};
        InvocationHandler handler = new RpcConsumerInvocationHandler(registry, interfaceReceiver);
        // service find

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, handler);



        return (T) helloProxy;
    }



    public Class<T> getInterface() {
        return interfaceReceiver;
    }

    public void setInterface(Class<T> interfaceReceiver) {
        this.interfaceReceiver = interfaceReceiver;
    }


    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
