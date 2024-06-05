package com.lavender.exceptions;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 17:36
 **/

public class ResponseException extends RuntimeException{
    private byte code;
    private String msg;
    public ResponseException() {
    }
    public ResponseException(byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public ResponseException(String message) {
        super(message);
    }

}
