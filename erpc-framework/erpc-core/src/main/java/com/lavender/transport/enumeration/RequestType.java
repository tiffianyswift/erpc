package com.lavender.transport.enumeration;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 12:24
 **/
public enum RequestType {
    REQUEST((byte)1, "normal rpc request"), HEARTBEAT((byte)2, "heartbeat health detect request");
    private byte id;
    private String type;

    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
