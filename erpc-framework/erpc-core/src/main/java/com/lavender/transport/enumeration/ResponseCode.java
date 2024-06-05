package com.lavender.transport.enumeration;

/**
 * 成功 20（call method success) 21(heartbeat sucess)
 * 负载 31 (server load very high, rate limited)
 * 错误 44 (client wrong code)
 * 错误 50 (server wrong code)
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 14:41
 **/
public enum ResponseCode {
    SUCCESS_METHOD_CALL((byte) 1, "success"),
    SUCESS_HEARTBEAT((byte) 21, ""),
    RATE_LIMITED((byte)31, ""),
    RESOUCES_NOT_FOUND((byte)44, ""),
    FAIL_METHOD_CALL((byte) 50, "fail");
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
