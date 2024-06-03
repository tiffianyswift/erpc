package com.lavender.serialiize;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 09:58
 **/

public interface Serializer {

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
