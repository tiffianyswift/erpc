package com.lavender.compress;

import com.lavender.serialiize.Serializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 11:23
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressorWraper {
    private byte code;
    private String type;
    private Compressor compressor;

}
