package com.lavender.loadbalancer.impl;

import com.lavender.ErpcBootStrap;
import com.lavender.exceptions.LoadBalancerException;
import com.lavender.loadbalancer.AbstractLoadBalancer;
import com.lavender.loadbalancer.Selector;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-03 16:34
 **/
@Slf4j
public class MinResponseTimeLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new MinResponseTimeSelector();
    }
    private static class MinResponseTimeSelector implements Selector{
        private List<InetSocketAddress> serviceList;
        private AtomicInteger index;
        public MinResponseTimeSelector(){

        }

        @Override
        public InetSocketAddress getNext() {
            Map.Entry<Long, Channel> entry = ErpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.firstEntry();
            if(entry == null){
                return (InetSocketAddress) ErpcBootStrap.CHANNEL_CACHE.values().iterator().next().remoteAddress();
            }
            return (InetSocketAddress) entry.getValue().remoteAddress();
        }

    }
}
