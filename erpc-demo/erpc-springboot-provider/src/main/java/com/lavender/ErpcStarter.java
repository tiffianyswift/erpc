package com.lavender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 17:15
 **/
@Component
@Slf4j
public class ErpcStarter implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(5000);
        ErpcBootStrap.getInstance().scan("com.lavender").start();
    }
}
