package com.lavender.proxy;

import com.lavender.Constant;
import com.lavender.ErpcBootStrap;
import com.lavender.ReferenceConfig;
import com.lavender.discovery.RegistryConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 17:46
 **/

public class ProxyFactory {
    public static Map<Class<?>, Object> cache = new ConcurrentHashMap<>(16);
    public static <T> T getProxy(Class<T> clazz){
        Object bean = cache.get(clazz);
        if(bean != null){
            return (T)bean;
        }
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setInterface(clazz);


        ErpcBootStrap.getInstance()
                .application("first-erpc-consumer")
                .registry(new RegistryConfig("zookeeper://"+ Constant.DEFAULT_ZK_CONNECT))
                .serialize("hessian")
                .compress("gzip")
                .group("primary")
                .reference(reference);

        T t =  reference.get();
        cache.put(clazz, t);
        return t;
    }
}
