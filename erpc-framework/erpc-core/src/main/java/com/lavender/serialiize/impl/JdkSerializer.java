package com.lavender.serialiize.impl;

import com.lavender.exceptions.SerializeException;
import com.lavender.serialiize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 10:50
 **/

@Slf4j
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if(object == null){
            return new byte[0];
        }
        //auto close
        try(
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(baos);
        ){
            log.debug("使用JDK完成【{}】的序列化过程", object);
            outputStream.writeObject(object);
            return baos.toByteArray();
        }
        catch (IOException e){
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if(bytes == null || clazz == null){
            return null;
        }
        //auto close
        try(
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream inputStream = new ObjectInputStream(bais);
        ){
            log.debug("以完成【{}】的反序列化过程", clazz);
            return (T) inputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e){
            log.error("反序列化【{}】过程出现异常", clazz, e);
            throw new SerializeException(e);
        }
    }
}
