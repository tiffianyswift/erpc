package com.lavender.channel.handler;

import com.lavender.ErpcBootStrap;
import com.lavender.transport.message.ErpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-31 21:17
 **/
@Slf4j
public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<ErpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ErpcResponse erpcResponse) throws Exception {
        Object returnValue = erpcResponse.getBody();
        CompletableFuture<Object> completableFuture = ErpcBootStrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(returnValue);
        if(log.isDebugEnabled()){
            log.debug("已寻找到编号为【{}】的completableFuture，处理响应结果。", erpcResponse.getResponseId());
        }
    }
}
