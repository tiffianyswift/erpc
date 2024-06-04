package com.lavender;

import com.lavender.discovery.RegistryConfig;
import com.lavender.impl.ExampleErpcImpl;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:03
 **/

public class ProviderApplication {
    public static void main(String[] args) {
        ;
        ErpcBootStrap.getInstance()
                .application("first-erpc-provider")
                .registry(new RegistryConfig("zookeeper://"+Constant.DEFAULT_ZK_CONNECT))
                .protocol(new ProtocolConfig("jdk"))
                .scan("com.lavender")
                .start();
    }
}
