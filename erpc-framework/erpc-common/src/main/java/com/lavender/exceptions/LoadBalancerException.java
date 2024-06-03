package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:52
 **/

public class LoadBalancerException extends RuntimeException{
    public LoadBalancerException() {

    }

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException(Throwable cause) {
        super(cause);
    }
}
