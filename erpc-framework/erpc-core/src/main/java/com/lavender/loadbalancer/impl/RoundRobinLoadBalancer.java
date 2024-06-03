package com.lavender.loadbalancer.impl;

import com.lavender.ErpcBootStrap;
import com.lavender.discovery.Registry;
import com.lavender.exceptions.LoadBalancerException;
import com.lavender.loadbalancer.AbstractLoadBalancer;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.loadbalancer.Selector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 19:53
 **/
@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new RoundRobinSelector(serviceList);
    }

    private static class RoundRobinSelector implements Selector{
        private List<InetSocketAddress> serviceList;
        private AtomicInteger index;
        public RoundRobinSelector(List<InetSocketAddress> serviceList){
            this.serviceList = serviceList;
            this.index = new AtomicInteger(0);
        }

        @Override
        public InetSocketAddress getNext() {
            if(serviceList == null || serviceList.isEmpty()){
                log.error("进行负载均衡时发生错误。");
                throw new LoadBalancerException();
            }
            InetSocketAddress address = serviceList.get(index.get());
            if(index.get() == serviceList.size() - 1){
                index.set(0);
            }
            else{
                index.incrementAndGet();
            }

            return address;
        }

    }
}
