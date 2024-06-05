package com.lavender.impl;


import com.lavender.ExampleErpc;
import com.lavender.annotation.ErpcImpl;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-29 16:42
 **/
@ErpcImpl
public class ExampleErpcImpl implements ExampleErpc {
    @Override
    public String saySo(String msg) {
        return "hi consumer:"+msg;
    }
}
