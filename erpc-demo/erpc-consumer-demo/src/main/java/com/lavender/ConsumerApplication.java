package com.lavender;

import com.lavender.core.HeartbeatDetector;
import com.lavender.discovery.RegistryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 21:07
 **/

@Slf4j
public class ConsumerApplication {
    public static void main(String[] args) {
        ReferenceConfig<ExampleErpc> reference = new ReferenceConfig<>();
        reference.setInterface(ExampleErpc.class);


        ErpcBootStrap.getInstance()
                .application("first-erpc-consumer")
                .registry(new RegistryConfig("zookeeper://"+Constant.DEFAULT_ZK_CONNECT))
                .serialize("hessian")
                .compress("gzip")
                .reference(reference);

        ExampleErpc exampleErpc = reference.get();
        String res = exampleErpc.saySo("so");
        log.error(res);
        System.out.println("开始心跳检测");
        HeartbeatDetector.detectHeartbeat(ExampleErpc.class.getName());
//        for(Map.Entry<String, ServiceConfig<?>> entry : ErpcBootStrap.SERVICES_LIST.entrySet()){
//            HeartbeatDetector.detectHeartbeat(entry.getKey());
//        }

    }
}
