package com.lavender.discovery;

import com.lavender.Constant;
import com.lavender.discovery.Registry;
import com.lavender.discovery.impl.NacosRegistry;
import com.lavender.discovery.impl.ZooKeeperRegistry;
import com.lavender.exceptions.DiscoverRegistryException;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:28
 **/

public class RegistryConfig {
    private final String connectString;
    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    public Registry getRegistry() {
        String registryType = getRegistryType(true).toLowerCase().trim();
        if(registryType.equals("zookeeper")){
            String host = getRegistryType(false);
            return new ZooKeeperRegistry(host, Constant.TIME_OUT);
        }
        else if (registryType.equals("nacos")){
            String host = getRegistryType(false);
            return new NacosRegistry(host, Constant.TIME_OUT);
        }
        throw new DiscoverRegistryException("未发现合适的注册中心");

    }
    private String getRegistryType(boolean istype){
        String[] typeAndHost = connectString.split("://");
        if(typeAndHost.length != 2){
            throw new RuntimeException("给定的url不合法");
        }
        if(istype){
            return typeAndHost[0];
        }
        else{
            return typeAndHost[1];
        }
    }
}
