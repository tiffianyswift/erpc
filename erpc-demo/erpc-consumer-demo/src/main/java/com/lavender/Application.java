package com.lavender;

import com.lavender.discovery.RegistryConfig;
import com.lavender.serialiize.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:07
 **/

@Slf4j
public class Application {
    public static void main(String[] args) {
        ReferenceConfig<ExampleErpc> reference = new ReferenceConfig<>();
        reference.setInterface(ExampleErpc.class);


        ErpcBootStrap.getInstance()
                .application("first-erpc-consumer")
                .registry(new RegistryConfig("zookeeper://"+Constant.DEFAULT_ZK_CONNECT))
                .serialize("hessian")
                .reference(reference);

        ExampleErpc exampleErpc = reference.get();
        String res = exampleErpc.saySo("so");
        log.error(res);
    }
}
