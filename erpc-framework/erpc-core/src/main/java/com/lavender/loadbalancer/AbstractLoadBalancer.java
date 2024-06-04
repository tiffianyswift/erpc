package com.lavender.loadbalancer;

import com.lavender.ErpcBootStrap;
import com.lavender.discovery.Registry;
import com.lavender.exceptions.LoadBalancerException;
import com.lavender.loadbalancer.impl.RoundRobinLoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 22:00
 **/
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer{
    private Map<String, Selector> cache = new ConcurrentHashMap<>(8);


    @Override
    public InetSocketAddress selectServiceAddress(String serviceName) {
        Selector selector = cache.get(serviceName);
        if(selector==null){
            List<InetSocketAddress> serviceList = ErpcBootStrap.getInstance().getConfiguration().getRegistryConfig().getRegistry().lookup(serviceName);
            selector = getSelector(serviceList);
            cache.put(serviceName, selector);
        }

        return selector.getNext();
    }
    @Override
    public synchronized void reLoadBalance(String serviceName, List<InetSocketAddress> addressList){
        cache.put(serviceName, getSelector(addressList));
    }


    protected abstract Selector getSelector(List<InetSocketAddress> serviceList);

}
