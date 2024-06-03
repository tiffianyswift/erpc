package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:52
 **/

public class CompressException extends RuntimeException{
    public CompressException() {

    }

    public CompressException(String message) {
        super(message);
    }

    public CompressException(Throwable cause) {
        super(cause);
    }
}
