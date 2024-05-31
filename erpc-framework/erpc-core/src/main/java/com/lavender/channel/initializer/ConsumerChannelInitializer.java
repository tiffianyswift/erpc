package com.lavender.channel.initializer;

import com.lavender.channel.handler.MySimpleChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-31 21:21
 **/

public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new MySimpleChannelInboundHandler());
    }
}
