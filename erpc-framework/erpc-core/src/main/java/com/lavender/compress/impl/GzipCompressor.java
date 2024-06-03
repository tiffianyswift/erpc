package com.lavender.compress.impl;

import com.lavender.compress.Compressor;
import com.lavender.exceptions.CompressException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 16:45
 **/
@Slf4j
public class GzipCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null || bytes.length == 0){
            return new byte[0];
        }
        try(
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        ){
            gzipOutputStream.write(bytes);
            gzipOutputStream.finish();
            byte[] result = baos.toByteArray();
            if(log.isDebugEnabled()){
                log.debug("对字节数据进行了压缩，长度由【{}】压缩至【{}】。", bytes.length, result.length);
            }
            return result;
        }
        catch (IOException e){
            log.error("对字节数据进行压缩时发生异常", e);
            throw new CompressException(e);

        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null || bytes.length == 0){
            return null;
        }
        try(
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        ){
            byte[] result = gzipInputStream.readAllBytes();
            if(log.isDebugEnabled()){
                log.debug("对字节数据进行了解压缩，长度由【{}】压缩至【{}】。", bytes.length, result.length);
            }
            return result;
        }
        catch (IOException e){
            log.error("对字节数据进行解压缩时发生异常", e);
            throw new CompressException(e);

        }
    }
}
