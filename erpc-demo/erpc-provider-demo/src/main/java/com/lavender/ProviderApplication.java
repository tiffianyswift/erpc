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

        ErpcBootStrap.getInstance().scan("com.lavender").start();
    }
}
