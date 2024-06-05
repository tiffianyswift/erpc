package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 17:36
 **/

public class SpiException extends RuntimeException{
    public SpiException() {
    }

    public SpiException(Throwable cause) {
        super(cause);
    }

    public SpiException(String message) {
        super(message);
    }
}
