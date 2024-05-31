package com.lavender;

import com.lavender.discovery.Registry;
import com.lavender.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

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
        Class[] classes = new Class[]{interfaceReceiver};

        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                System.out.println(args[0]);
                InetSocketAddress address = registry.lookup(interfaceReceiver.getName());
                if(log.isDebugEnabled()){
                    log.debug("服务调用方， 发现了服务【{}】的可用主机【{}】。", interfaceReceiver.getName(), address);
                }
                return null;
            }
        });
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
