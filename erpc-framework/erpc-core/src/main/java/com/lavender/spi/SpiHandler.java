package com.lavender.spi;

import com.lavender.config.Configuration;
import com.lavender.config.ObjectWrapper;
import com.lavender.exceptions.SpiException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 09:37
 **/
@Slf4j
public class SpiHandler {
    private static final String BASE_PATH = "META-INF/erpc-services";
    private static final Map<String, List<String>> SPI_CONTENT = new ConcurrentHashMap<>(16);
    private static final Map<Class<?>, List<ObjectWrapper<?>>> SPI_IMPLEMENT = new ConcurrentHashMap<>(32);
    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL fileUrl = classLoader.getResource(BASE_PATH);
        if(fileUrl != null){
            File file = new File(fileUrl.getPath());
            File[] children = file.listFiles();
            if(children != null){
                for(File child : children){
                    String key  = child.getName();
                    List<String> value = getImplNames(child);
                    SPI_CONTENT.put(key, value);
                }
            }
        }
    }

    private static List<String> getImplNames(File child) {
        try(
                FileReader fileReader = new FileReader(child);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                ){
            List<String> implNames = new ArrayList<>();
            while(true){
                String line = bufferedReader.readLine();
                if(line == null || "".equals(line)){
                    break;
                }
                implNames.add(line);

            }
            return implNames;

        }
        catch (IOException e){
            log.error("读取spi文件时发生异常", e);
        }
        return null;
    }

    public static <T> ObjectWrapper<T> get(Class<T> clazz){
        List<ObjectWrapper<?>> objectWrappers = SPI_IMPLEMENT.get(clazz);
        if(objectWrappers != null && !objectWrappers.isEmpty()){
            return (ObjectWrapper<T>) objectWrappers.get(0);
        }
        buildCache(clazz);
        List<ObjectWrapper<?>> result = SPI_IMPLEMENT.get(clazz);
        if(result == null || result.isEmpty()){
            return null;
        }

        return (ObjectWrapper<T>) result.get(0);
    }

    public synchronized static <T> List<ObjectWrapper<T>> getList(Class<T> clazz){
        List<ObjectWrapper<?>> objectWrappers = SPI_IMPLEMENT.get(clazz);
        if(objectWrappers != null && !objectWrappers.isEmpty()){
            return objectWrappers.stream().map(objectWrapper -> (ObjectWrapper<T>)objectWrapper).collect(Collectors.toList());
        }
        buildCache(clazz);
        objectWrappers = SPI_IMPLEMENT.get(clazz);
        if(objectWrappers != null && !objectWrappers.isEmpty()){
            return objectWrappers.stream().map(objectWrapper -> (ObjectWrapper<T>)objectWrapper).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private static void buildCache(Class<?> clazz) {
        String name = clazz.getName();
        List<String> implNames = SPI_CONTENT.get(name);
        if(implNames == null || implNames.isEmpty()){
            return;
        }
        List<ObjectWrapper<?>> impls = new ArrayList<>();
        for (String implName : implNames) {
            try {
                String[] codeAndTypeAndName = implName.split(" ");
                if(codeAndTypeAndName.length != 3){
                    throw new SpiException("配置的spi文件不合法。");
                }
                Byte code = Byte.valueOf(codeAndTypeAndName[0]);
                String type = codeAndTypeAndName[1];
                String implementName = codeAndTypeAndName[2];

                Class<?> aClass = Class.forName(implementName);
                Object impl = aClass.getConstructor().newInstance();
                ObjectWrapper<?> objectWrapper = new ObjectWrapper<>(code, type, impl);
                impls.add(objectWrapper);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException | InstantiationException e) {
                log.error("实例化【{}】的过程发生了异常", implName, e);
            }
        }
        SPI_IMPLEMENT.put(clazz, impls);
    }


    public static void main(String[] args) {


    }
}
