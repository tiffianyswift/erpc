package com.lavender.protection;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 15:28
 **/

public class TokenBucketRateLimiter implements RateLimiter{
    private int tokens;
    private final int capacity;
    private final int rate;
    private Long lastTokenTime = System.currentTimeMillis();
    public TokenBucketRateLimiter(int capacity, int rate){
        this.capacity = capacity;
        this.rate = rate;
        lastTokenTime = System.currentTimeMillis();
        tokens = capacity;
    }
    public synchronized boolean allowRequest(){
        Long currentTime = System.currentTimeMillis();
        Long timeInterval = currentTime - lastTokenTime;
        if(timeInterval >= 1000/rate){
            int needAddTokens = (int)(timeInterval * rate / 1000);
            tokens = Math.min(capacity, tokens+needAddTokens);
            this.lastTokenTime = System.currentTimeMillis();

        }

        if(tokens > 0){
            tokens--;
            return true;
        }
        else{
            return false;
        }

    }

    public static void main(String[] args) {
        RateLimiter rateLimiter = new TokenBucketRateLimiter(5, 1);
        for (int i = 0; i < 300; i++) {
            System.out.println("rateLimiter.allowRequest() = " + rateLimiter.allowRequest());

        }
    }
}
