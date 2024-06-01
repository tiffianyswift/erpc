package com.lavender.channel.handler;

import com.lavender.ErpcBootStrap;
import com.lavender.ServiceConfig;
import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.ErpcRequestPayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 12:05
 **/
@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<ErpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ErpcRequest erpcRequest) throws Exception {
        ErpcRequestPayload requestPayload = erpcRequest.getRequestPayload();

        Object object = callTargetMethod(requestPayload);
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
