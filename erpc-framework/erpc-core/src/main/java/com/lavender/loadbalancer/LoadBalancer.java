package com.lavender.loadbalancer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 19:55
 **/
public interface LoadBalancer {
    InetSocketAddress selectServiceAddress(String serviceName, String group);
    void reLoadBalance(String serviceName, List<InetSocketAddress> addressList);
}
