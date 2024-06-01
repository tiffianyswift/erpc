package com.lavender.transport.enumeration;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 14:41
 **/
public enum ResponseCode {
    SUCCESS((byte) 1, "success"), FAIL((byte) 2, "fail");
    private byte code;
    private String desc;

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    ResponseCode(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
