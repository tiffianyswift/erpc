package com.lavender.discovery.impl;

import com.lavender.ServiceConfig;
import com.lavender.discovery.AbstractRegistry;

import java.net.InetSocketAddress;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:46
 **/

public class NacosRegistry extends AbstractRegistry {
    public NacosRegistry(String host, int timeout) {
        super();
    }

    @Override
    public void register(ServiceConfig<?> serviceConfig) {

    }

    @Override
    public InetSocketAddress lookup(String name) {
        return null;
    }
}
