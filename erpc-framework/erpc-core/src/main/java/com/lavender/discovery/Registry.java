package com.lavender.discovery;

import com.lavender.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:25
 **/

public interface Registry {
    /**
     * service register
     * @param serviceConfig
     */
    void register(ServiceConfig<?> serviceConfig);

    /**
     * service discover
     * @param name
     * @return ip+port
     */
    List<InetSocketAddress> lookup(String name, String group);
}
