package com.lavender.config;

import com.lavender.compress.Compressor;
import com.lavender.compress.CompressorFactory;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.serialiize.Serializer;
import com.lavender.serialiize.SerializerFactory;
import com.lavender.spi.SpiHandler;

import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 09:25
 **/

public class SpiResolver {
    public void loadFromSpi(Configuration configuration){
        List<ObjectWrapper<LoadBalancer>> loadBalancerObjectWrappers = SpiHandler.getList(LoadBalancer.class);
        if(loadBalancerObjectWrappers != null && !loadBalancerObjectWrappers.isEmpty()){
            configuration.setLoadBalancer(loadBalancerObjectWrappers.get(0).getImpl());
        }


        List<ObjectWrapper<Compressor>> compressorObjectWrappers = SpiHandler.getList(Compressor.class);
        if(compressorObjectWrappers != null){
            compressorObjectWrappers.forEach(CompressorFactory::addCompresssor);
        }


        List<ObjectWrapper<Serializer>> serializerObjectWrapper = SpiHandler.getList(Serializer.class);
        if(serializerObjectWrapper != null){
            serializerObjectWrapper.forEach(SerializerFactory::addSerializer);
        }



    }
}
