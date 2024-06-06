package com.lavender.core;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 14:17
 **/

public class ErpcShutdownHook extends Thread{
    @Override
    public void run(){
        // build a barrier to avoid handle new request
        ShutdownHolder.BAFFLE.set(true);
        long start = System.currentTimeMillis();
        // wait the request count to zero
        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(ShutdownHolder.REQUEST_COUNTER.sum() == 0L || System.currentTimeMillis() - start > 10*1000){
                break;
            }
        }

        //
    }
}
