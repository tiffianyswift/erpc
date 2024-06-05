package com.lavender.core;

import com.lavender.config.Configuration;
import com.lavender.ErpcBootStrap;
import com.lavender.compress.CompressorFactory;
import com.lavender.discovery.NettyBootstrapInitializer;
import com.lavender.discovery.Registry;
import com.lavender.serialiize.SerializerFactory;
import com.lavender.transport.enumeration.RequestType;
import com.lavender.transport.message.ErpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-03 11:15
 **/
@Slf4j
public class HeartbeatDetector {
    public static void detectHeartbeat(String ServiceName){
        Registry registry = ErpcBootStrap.getInstance().getConfiguration().getRegistryConfig().getRegistry();
        List<InetSocketAddress> addresses = registry.lookup(ServiceName);

        for(InetSocketAddress address : addresses){
            try {
                if(!ErpcBootStrap.CHANNEL_CACHE.containsKey(address)){
                    Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                    ErpcBootStrap.CHANNEL_CACHE.put(address, channel);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Thread thread = new Thread(()->{
            new Timer().schedule(new MyTimerTask(), 0, 2000);
        }, "erpc-heartbeatDetector-thread");
        thread.setDaemon(true);
        thread.start();
    }
    private static class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            ErpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.clear();
            Map<InetSocketAddress, Channel> cache = ErpcBootStrap.CHANNEL_CACHE;
            Configuration configuration = ErpcBootStrap.getInstance().getConfiguration();
            int tryTimes = 3;
            for(Map.Entry<InetSocketAddress, Channel> entry: cache.entrySet()){
                while(0 < tryTimes){
                    Channel channel = entry.getValue();
                    long start = System.currentTimeMillis();
                    ErpcRequest erpcRequest = ErpcRequest.builder()
                            .requestId(configuration.getIdGenerator().getId())
                            .compressType(CompressorFactory.getCompressorWraper(configuration.getCompressType()).getCode())
                            .requestType(RequestType.HEARTBEAT.getId())
                            .serializeType(SerializerFactory.getSerializerWraper(configuration.getSerializeType()).getCode())
                            .timeStamp(new Date().getTime())
                            .build();

                    CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                    ErpcBootStrap.PENDING_REQUEST.put(erpcRequest.getRequestId(), completableFuture);
                    channel.writeAndFlush(erpcRequest).addListener((ChannelFutureListener) promise ->{
                        if(!promise.isSuccess()){
                            completableFuture.completeExceptionally(promise.cause());
                        }
                    });

                    long end = 0;
                    try {
                        completableFuture.get();
                        end = System.currentTimeMillis();
                    } catch (InterruptedException | ExecutionException e) {
                        tryTimes --;
                        log.error("和地址为【{}】的主机连接发生异常。", channel.remoteAddress());

                        if(tryTimes == 0){
                            ErpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                        }
                        try {
                            Thread.sleep(10L *(3-tryTimes));
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        continue;

                    }
                    long time = end - start;
                    ErpcBootStrap.ANSWER_TIME_CHANNEL_CACHE.put(time, channel);
                    log.info("和【{}】响应时间为【{}】", entry.getKey(), time);
                    break;
                }


            }

        }
    }
}
