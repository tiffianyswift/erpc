package com.lavender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 17:07
 **/
@SpringBootApplication
@RestController
public class AplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(AplicationConsumer.class, args);
    }
}