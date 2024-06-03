package com.lavender.loadbalancer.impl;

import com.lavender.ErpcBootStrap;
import com.lavender.exceptions.LoadBalancerException;
import com.lavender.loadbalancer.AbstractLoadBalancer;
import com.lavender.loadbalancer.Selector;
import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.MessageConstant;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.hash;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 19:53
 **/
@Slf4j
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new ConsistentHashSelector(serviceList, 128);
    }

    private static class ConsistentHashSelector implements Selector{
        private SortedMap<Integer, InetSocketAddress> hashCircle = new TreeMap<>();
        private int virtualNodes;
        public ConsistentHashSelector(List<InetSocketAddress> serviceList, int virtualNodes){
            this.virtualNodes = virtualNodes;
            for(InetSocketAddress inetSocketAddress : serviceList){
                addNodeToCircle(inetSocketAddress);
            }
        }

        private void addNodeToCircle(InetSocketAddress inetSocketAddress) {
            // for every node, generate virtual node
            for (int i = 0; i < virtualNodes; i++) {
                int hashVal = hash(inetSocketAddress.toString() + "-" + i);
                hashCircle.put(hashVal, inetSocketAddress);
            }
        }
        private void removeNodeToCircle(InetSocketAddress inetSocketAddress) {
            // for every node, generate virtual node
            for (int i = 0; i < virtualNodes; i++) {
                int hashVal = hash(inetSocketAddress.toString() + "-" + i);
                hashCircle.remove(hashVal, inetSocketAddress);
            }
        }

        private int Mdhash(String s){
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] digest = md.digest(s.getBytes());
            int res = 0;
            for (int i = 0; i < 4; i++) {
                res = res << 8;
                if(digest[i]<0){
                    res = res | (digest[i] & 255);
                }
                else{
                    res = res | digest[i];
                }

            }
            return res;
        }

        @Override
        public InetSocketAddress getNext() {
            ErpcRequest erpcRequest = ErpcBootStrap.REQUEST_THREAD_LOCAL.get();
            String requestId = Long.toString(erpcRequest.getRequestId());
            int hashVal = Mdhash(requestId);
            if(!hashCircle.containsKey(hashVal)){
                SortedMap<Integer, InetSocketAddress> tailMap = hashCircle.tailMap(hashVal);
                hashVal = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
            }

            return hashCircle.get(hashVal);
        }

    }
}
