package com.lavender;

import com.lavender.discovery.Registry;
import com.lavender.discovery.RegistryConfig;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.loadbalancer.impl.RoundRobinLoadBalancer;
import com.lavender.transport.message.ErpcRequest;
import io.netty.channel.Channel;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-04 09:56
 **/
@Data
public class Configuration {
    private int port = 8098;
    private String applicationName = "default";
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private IDGenerator idGenerator = new IDGenerator(1, 1);
    private String serializeType = "jdk";
    private String compressType = "gzip";

    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    public Configuration(){

    }




}
