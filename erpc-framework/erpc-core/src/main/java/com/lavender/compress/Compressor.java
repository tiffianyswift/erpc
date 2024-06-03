package com.lavender.compress;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 16:40
 **/
public interface Compressor {
    /**
     * compress
     * @param bytes
     * @return
     */
    byte[] compress(byte[] bytes);

    /**
     * decompress
     * @param bytes
     * @return
     */
    byte[] decompress(byte[] bytes);
}
