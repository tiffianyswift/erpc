package com.lavender;

import com.lavender.annotation.ErpcService;
import com.lavender.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 17:33
 **/
@Component
public class ErpcProxyBeanPostProcessor implements BeanPostProcessor {
    // 他会拦截所有bean的创建，会在每一个bean初始化后被调用
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            ErpcService erpcService = field.getAnnotation(ErpcService.class);
            if(erpcService != null){
                Class<?> type = field.getType();
                Object proxy = ProxyFactory.getProxy(type);
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
