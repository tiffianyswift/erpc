package com.lavender.transport.message;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 08:51
 **/

public class MessageConstant {
    public final static byte[] MAGIC_NUM = "erpc".getBytes();
    public final static byte VERSION = 1;
    public final static short HEADER_LENGTH = (short)(MAGIC_NUM.length + 1 + 2 + 4 + 1 + 1 + 1 + 8 + 8);
    public final static int MAX_FRAME_LENGTH = 1024*1024;

    public static final int VERSION_LENGTH = 1;
    public static final int HEADER_FIELD_LENGTH = 2;
    public static final int FULL_FIELD_LENGTH = 4;
}
