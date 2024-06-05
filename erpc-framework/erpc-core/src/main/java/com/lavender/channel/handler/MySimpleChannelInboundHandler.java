package com.lavender.channel.handler;

import com.lavender.ErpcBootStrap;
import com.lavender.exceptions.ResponseException;
import com.lavender.protection.CircuitBreaker;
import com.lavender.transport.enumeration.ResponseCode;
import com.lavender.transport.message.ErpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Map;
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
        SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();
        Map<SocketAddress, CircuitBreaker> ipCircuitBreaker = ErpcBootStrap.getInstance().getConfiguration().getIpCircuitBreaker();
        CircuitBreaker circuitBreaker = ipCircuitBreaker.get(socketAddress);
        byte code = erpcResponse.getCode();
        CompletableFuture<Object> completableFuture = ErpcBootStrap.PENDING_REQUEST.get(erpcResponse.getResponseId());
        if(code == ResponseCode.FAIL_METHOD_CALL.getCode()){
            circuitBreaker.recordErrorRequest();;
            completableFuture.complete(null);
            log.error("当前id为【{}】的请求，返回错误的结果，响应码【{}】", erpcResponse.getResponseId(), erpcResponse.getCode());
            throw new ResponseException(code, ResponseCode.FAIL_METHOD_CALL.getDesc());
        }
        else if(code == ResponseCode.RATE_LIMITED.getCode()){
            circuitBreaker.recordErrorRequest();
            completableFuture.complete(null);
            log.error("当前id为【{}】的请求被限流，响应码【{}】", erpcResponse.getResponseId(), erpcResponse.getCode());
            throw new ResponseException(code, ResponseCode.RATE_LIMITED.getDesc());
        }
        else if(code == ResponseCode.RESOUCES_NOT_FOUND.getCode()){
            circuitBreaker.recordErrorRequest();
            completableFuture.complete(null);
            log.error("当前id为【{}】的请求未找到相应资源，响应码【{}】", erpcResponse.getResponseId(), erpcResponse.getCode());
            throw new ResponseException(code, ResponseCode.RESOUCES_NOT_FOUND.getDesc());
        }
        else if(code == ResponseCode.SUCCESS_METHOD_CALL.getCode()){
            Object returnValue = erpcResponse.getBody();
            // todo code 区分。
            if(returnValue == null){
                returnValue = new Object();
            }

            completableFuture.complete(returnValue);
            if(log.isDebugEnabled()){
                log.debug("已寻找到编号为【{}】的completableFuture，处理响应结果。", erpcResponse.getResponseId());
            }
        }
        else if(code == ResponseCode.SUCESS_HEARTBEAT.getCode()){
            completableFuture.complete(null);
            log.error("当前id为【{}】的请求未找到相应资源，处理心跳", erpcResponse.getResponseId(), erpcResponse.getCode());
        }

    }
}
