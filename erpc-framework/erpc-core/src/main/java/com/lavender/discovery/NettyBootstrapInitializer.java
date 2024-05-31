package com.lavender.discovery;

import com.lavender.ErpcBootStrap;
import com.lavender.channel.handler.MySimpleChannelInboundHandler;
import com.lavender.channel.initializer.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-31 19:11
 **/
@Slf4j
public class NettyBootstrapInitializer {
    private static Bootstrap bootstrap = new Bootstrap();
    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = bootstrap.group(group).channel(NioSocketChannel.class).handler(new ConsumerChannelInitializer());
    }
    private static NioEventLoopGroup group = new NioEventLoopGroup();
    private NettyBootstrapInitializer(){

    }
    public static Bootstrap getBootstrap(){
        return bootstrap;
    }
}
