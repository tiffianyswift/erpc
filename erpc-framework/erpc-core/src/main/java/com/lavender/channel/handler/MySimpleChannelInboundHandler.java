package com.lavender.channel.handler;

import com.lavender.ErpcBootStrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-31 21:17
 **/

public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        String result = msg.toString(Charset.defaultCharset());
        CompletableFuture<Object> completableFuture = ErpcBootStrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(result);
    }
}
