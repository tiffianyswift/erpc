package com.lavender.serialiize;

import com.lavender.serialiize.impl.HessianSerializer;
import com.lavender.serialiize.impl.JdkSerializer;
import com.lavender.serialiize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 11:14
 **/
@Slf4j
public class SerializerFactory {
    public final static ConcurrentHashMap<String, SerializerWraper> SERIALIZER_NAME_CACHE = new ConcurrentHashMap<>(16);
    public final static ConcurrentHashMap<Byte, SerializerWraper> SERIALIZER_CODE_CACHE = new ConcurrentHashMap<>(16);
    private static final String DEFAULT_SERIALIZER_NAME = "jdk";
    private static final byte DEFAULT_SERIALIZER_CODE = 1;
    static {
        SerializerWraper jdk = new SerializerWraper((byte) 1, "jdk", new JdkSerializer());
        SerializerWraper json = new SerializerWraper((byte) 2, "json", new JsonSerializer());
        SerializerWraper hessian = new SerializerWraper((byte) 3, "hessian", new HessianSerializer());
        SERIALIZER_NAME_CACHE.put("jdk", jdk);
        SERIALIZER_NAME_CACHE.put("json", json);
        SERIALIZER_NAME_CACHE.put("hessian", hessian);
        SERIALIZER_CODE_CACHE.put((byte)1, jdk);
        SERIALIZER_CODE_CACHE.put((byte)2, json);
        SERIALIZER_CODE_CACHE.put((byte)3, hessian);

    }
    public static SerializerWraper getSerializerWraper(String serializeType) {
        SerializerWraper serializerWraper = SERIALIZER_NAME_CACHE.get(serializeType);
        if(serializerWraper == null){
            if(log.isDebugEnabled()){
                log.debug("未找到名称为【{}】的序列化协议，已使用默认的序列化协议【{}】", serializeType, DEFAULT_SERIALIZER_NAME);
            }
            return SERIALIZER_NAME_CACHE.get(DEFAULT_SERIALIZER_NAME);
        }
        return serializerWraper;
    }
    public static SerializerWraper getSerializerWraper(byte serializeCode) {
        SerializerWraper serializerWraper = SERIALIZER_CODE_CACHE.get(serializeCode);
        if(serializerWraper == null){
            if(log.isDebugEnabled()){
                log.debug("未找到名称为【{}】的序列化协议，已使用默认的序列化协议【{}】", serializeCode, DEFAULT_SERIALIZER_CODE);
            }
            return SERIALIZER_CODE_CACHE.get(DEFAULT_SERIALIZER_CODE);
        }
        return serializerWraper;
    }

}
