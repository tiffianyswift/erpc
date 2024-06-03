package com.lavender.compress;

import com.lavender.compress.impl.GzipCompressor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 11:14
 **/
@Slf4j
public class CompressorFactory {
    public final static ConcurrentHashMap<String, CompressorWraper> COMPRESSOR_NAME_CACHE = new ConcurrentHashMap<>(16);
    public final static ConcurrentHashMap<Byte, CompressorWraper> COMPRESSOR_CODE_CACHE = new ConcurrentHashMap<>(16);
    private final static byte DEFAULT_COMPRESSOR_CODE = 1;
    private final static String DEFAULT_COMPRESSOR_NAME = "gzip";
    static {
        CompressorWraper gzip = new CompressorWraper((byte) 1, "gzip", new GzipCompressor());

        COMPRESSOR_NAME_CACHE.put("gzip", gzip);

        COMPRESSOR_CODE_CACHE.put((byte)1, gzip);


    }
    public static CompressorWraper getCompressorWraper(String compressType) {
        CompressorWraper compressorWraper = COMPRESSOR_NAME_CACHE.get(compressType);
        if(compressorWraper == null){
            if(log.isDebugEnabled()){
                log.debug("未找到名称为【{}】的压缩协议，已使用默认的压缩协议【{}】", compressType, DEFAULT_COMPRESSOR_NAME);
            }
            return COMPRESSOR_NAME_CACHE.get(DEFAULT_COMPRESSOR_NAME);
        }
        return compressorWraper;
    }
    public static CompressorWraper getCompressorWraper(byte compressCode) {
        CompressorWraper compressorWraper = COMPRESSOR_CODE_CACHE.get(compressCode);
        if(compressorWraper == null){
            if(log.isDebugEnabled()){
                log.debug("未找到名称为【{}】的压缩协议，已使用默认的压缩协议【{}】", compressCode, DEFAULT_COMPRESSOR_CODE);
            }
            return COMPRESSOR_CODE_CACHE.get(DEFAULT_COMPRESSOR_CODE);
        }
        return compressorWraper;
    }

}
