package com.lavender.serialiize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.lavender.exceptions.SerializeException;
import com.lavender.serialiize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 13:13
 **/
@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if(object == null){
            return new byte[0];
        }
        //auto close
        try(
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ){
            Hessian2Output hessian2Output = new Hessian2Output(baos);
            hessian2Output.writeObject(object);
            hessian2Output.flush();

            if(log.isDebugEnabled()){
                log.debug("已使用hessian完成【{}】的序列化过程", object);
            }

            return baos.toByteArray();
        }
        catch (IOException e){
            log.error("序列化【{}】发生异常", object);
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
        ){
            Hessian2Input hessian2Input = new Hessian2Input(bais);
            T object = (T)hessian2Input.readObject();

            if(log.isDebugEnabled()){
                log.debug("已使用hessian完成【{}】的反序列化过程", clazz);
            }

            return object;
        }
        catch (IOException e){
            log.error("序列化【{}】发生异常", clazz);
            throw new SerializeException(e);
        }
    }
}
