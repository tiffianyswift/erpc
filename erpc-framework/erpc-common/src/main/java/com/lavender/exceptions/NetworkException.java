package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 17:36
 **/

public class NetworkException extends RuntimeException{
    public NetworkException() {
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message) {
        super(message);
    }
}
