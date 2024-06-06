package com.lavender;

import com.lavender.annotation.ErpcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 17:27
 **/
@RestController
public class HelloController {
    @ErpcService
    private ExampleErpc exampleErpc;

    @GetMapping("hello")
    public String hello(){
        return exampleErpc.saySo("provider");
    }
}
