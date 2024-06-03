package com.lavender.serialiize.impl;

import com.alibaba.fastjson2.JSON;
import com.lavender.serialiize.Serializer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 11:16
 **/
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if(object == null){
            return new byte[0];
        }
        if(log.isDebugEnabled()){
            log.debug("使用Json完成【{}】的序列化过程", object);
        }

        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes == null || clazz == null){
            return null;
        }
        T t = JSON.parseObject(bytes, clazz);
        if(log.isDebugEnabled()){
            log.debug("已使用JSON完成【{}】的反序列化过程", clazz);
        }
        return t;
    }
}
