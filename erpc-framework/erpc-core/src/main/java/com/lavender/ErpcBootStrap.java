package com.lavender;


import com.lavender.discovery.Registry;
import com.lavender.discovery.RegistryConfig;
import com.lavender.discovery.impl.ZooKeeperRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:13
 **/
@Slf4j
public class ErpcBootStrap {
    private static ErpcBootStrap erpcBootStrap = new ErpcBootStrap();
    private String applicationName = "default";
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private int port = 8088;
    private Registry registry;
    private static final Map<String, ServiceConfig<?>> SERVICES_LIST = new HashMap<>(16);

    private ErpcBootStrap(){


    }

    public static ErpcBootStrap getInstance() {
        return erpcBootStrap;
    }

    /**
     * define name of temparary appName
     * @param appName
     * @return
     */
    public ErpcBootStrap application(String appName) {
        this.applicationName = appName;
        return this;
    }

    /**
     * config a registry center
     * @param registryConfig
     * @return
     */
    public ErpcBootStrap registry(RegistryConfig registryConfig) {
        this.registry = registryConfig.getRegistry();
        this.registryConfig = registryConfig;
        return this;
    }

    /**
     * select a sequecelize protocol
     * @param protocolConfig
     * @return
     */
    public ErpcBootStrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if(log.isDebugEnabled()){
            log.debug("当前工程使用了，{}协议进行序列化", protocolConfig.toString());
        }
        return this;
    }

    /**
     * publish a service
     * @param service
     * @return
     */
    public ErpcBootStrap publish(ServiceConfig<?> service) {
        registry.register(service);
        SERVICES_LIST.put(service.getInterface().getName(), service);
        return this;
    }

    public ErpcBootStrap publish(List<ServiceConfig<?>> services) {
        for(ServiceConfig<?> service : services){
            this.publish(service);
        }
        return this;
    }


    /**
     * start netty service
     */
    public void start() {
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ErpcBootStrap reference(ReferenceConfig<?> reference) {
        reference.setRegistry(registry);

        return this;
    }
}
