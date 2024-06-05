package com.lavender.protection;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 17:36
 **/

public interface RateLimiter {
    boolean allowRequest();
}
