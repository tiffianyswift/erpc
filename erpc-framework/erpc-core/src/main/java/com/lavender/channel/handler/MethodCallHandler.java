package com.lavender.channel.handler;

import com.lavender.ErpcBootStrap;
import com.lavender.ServiceConfig;
import com.lavender.core.ShutdownHolder;
import com.lavender.protection.RateLimiter;
import com.lavender.protection.TokenBucketRateLimiter;
import com.lavender.transport.enumeration.RequestType;
import com.lavender.transport.enumeration.ResponseCode;
import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.ErpcRequestPayload;
import com.lavender.transport.message.ErpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 12:05
 **/
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<ErpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ErpcRequest erpcRequest) throws Exception {
        ErpcResponse erpcResponse = ErpcResponse.builder()
                .responseId(erpcRequest.getRequestId())
                .compressType(erpcRequest.getCompressType())
                .serializeType(erpcRequest.getSerializeType())
                .timeStamp(new Date().getTime()).build();

        Channel channel = channelHandlerContext.channel();

        if(ShutdownHolder.BAFFLE.get()){
            erpcResponse.setCode(ResponseCode.CLOSE_WAIT.getCode());
            channel.writeAndFlush(erpcResponse);
            return;
        }

        ShutdownHolder.REQUEST_COUNTER.increment();

        SocketAddress socketAddress = channel.remoteAddress();
        Map<SocketAddress, RateLimiter> ipRateLimiter = ErpcBootStrap.getInstance().getConfiguration().getIpRateLimiter();
        RateLimiter rateLimiter = ipRateLimiter.get(socketAddress);

        if(rateLimiter == null){
            rateLimiter = new TokenBucketRateLimiter(100, 100);
            ipRateLimiter.put(socketAddress, rateLimiter);
        }
        boolean allowRequest = rateLimiter.allowRequest();
        if(!allowRequest){
            erpcResponse.setBody(null);
            erpcResponse.setCode(ResponseCode.RATE_LIMITED.getCode());

        }
        else if(erpcRequest.getRequestType() == RequestType.HEARTBEAT.getId()){
            erpcResponse.setBody(null);
            erpcResponse.setCode(ResponseCode.SUCESS_HEARTBEAT.getCode());

        }
        else{
            try {
                ErpcRequestPayload requestPayload = erpcRequest.getRequestPayload();
                Object result = callTargetMethod(requestPayload);
                if (log.isDebugEnabled()) {
                    log.debug("请求【{}】已经在服务端完成调用。", erpcRequest.getRequestId());
                }
                erpcResponse.setCode(ResponseCode.SUCCESS_METHOD_CALL.getCode());
                erpcResponse.setBody(result);
            }catch (Exception e){
                log.error("编号为【{}】的请求在调用过程中发生异常[{}]", erpcRequest.getRequestId(), erpcRequest.getRequestType(), e);
                erpcResponse.setCode(ResponseCode.FAIL_METHOD_CALL.getCode());
            }


        }
        channelHandlerContext.channel().writeAndFlush(erpcResponse);
        ShutdownHolder.REQUEST_COUNTER.decrement();
    }

    private Object callTargetMethod(ErpcRequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methoadName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();

        ServiceConfig<?> serviceConfig = ErpcBootStrap.SERVICES_LIST.get(interfaceName);
        Object refImpl = serviceConfig.getRef();
        Object returnValue = null;
        try {
            Class<?> clazz = refImpl.getClass();
            Method method = clazz.getMethod(methoadName, parametersType);
            returnValue = method.invoke(refImpl, parametersValue);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用服务【{}】的方法【{}】时发生了异常", interfaceName, methoadName, e);
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
