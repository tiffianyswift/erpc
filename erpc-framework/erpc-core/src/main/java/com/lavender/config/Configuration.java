package com.lavender.config;

import com.lavender.IDGenerator;
import com.lavender.discovery.RegistryConfig;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.loadbalancer.impl.RoundRobinLoadBalancer;
import com.lavender.protection.CircuitBreaker;
import com.lavender.protection.RateLimiter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-04 09:56
 **/
@Data
@Slf4j
public class Configuration {
    private int port = 8098;
    private String applicationName = "default";
    private RegistryConfig registryConfig = new RegistryConfig("zookeeper://49.235.128.207:2181");

    private String serializeType = "jdk";

    private String compressType = "gzip";

    private String group = "default";

    private IDGenerator idGenerator = new IDGenerator(1, 1);



    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
    private final Map<SocketAddress, RateLimiter> ipRateLimiter = new ConcurrentHashMap<>();
    private final Map<SocketAddress, CircuitBreaker> ipCircuitBreaker = new ConcurrentHashMap<>();

    public Configuration(){
        // 1, 成员变量
        SpiResolver spiResolver = new SpiResolver();
        spiResolver.loadFromSpi(this);

        XmlResolver xmlResolver = new XmlResolver();
        xmlResolver.loadFromXml(this);

    }




    public static void main(String[] args) {
        Configuration configuration = new Configuration();
    }


}
