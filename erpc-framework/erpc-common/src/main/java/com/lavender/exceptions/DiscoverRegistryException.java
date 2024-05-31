package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:52
 **/

public class DiscoverRegistryException extends RuntimeException{
    public DiscoverRegistryException() {
    }

    public DiscoverRegistryException(String message) {
        super(message);
    }

    public DiscoverRegistryException(Throwable cause) {
        super(cause);
    }
}
