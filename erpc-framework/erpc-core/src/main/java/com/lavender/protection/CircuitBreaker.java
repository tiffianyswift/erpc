package com.lavender.protection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 16:16
 **/

public class CircuitBreaker {
    private volatile boolean isOpen = false;
    private AtomicInteger requestCount = new AtomicInteger(0);
    private AtomicInteger errorRequest = new AtomicInteger(0);
    private int maxErrorRequest;
    private float maxErrorRate;
    public CircuitBreaker(int maxErrorRequest, float maxErrorRate){
        this.maxErrorRequest = maxErrorRequest;
        this.maxErrorRate = maxErrorRate;
    }
    public void recordRequest(){
        this.requestCount.getAndIncrement();
    }
    public void recordErrorRequest(){
        this.errorRequest.getAndIncrement();
    }
    public boolean isBreak(){
        if(isOpen){
            return true;
        }
        if(errorRequest.get() > maxErrorRequest){
            this.isOpen = true;
            return true;
        }
        if(errorRequest.get() > 0 && requestCount.get() > 0 && errorRequest.get()/(float)requestCount.get() > maxErrorRate){
            this.isOpen = true;
            return true;
        }

        return false;
    }
    public void reset(){
        this.isOpen = false;
        this.requestCount.set(0);
        this.errorRequest.set(0);
    }
}
