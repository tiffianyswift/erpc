package com.lavender.impl;


import com.lavender.ExampleErpc;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 16:42
 **/

public class ExampleErpcImpl implements ExampleErpc {
    @Override
    public String saySo(String msg) {
        return "hi consumer:"+msg;
    }
}
