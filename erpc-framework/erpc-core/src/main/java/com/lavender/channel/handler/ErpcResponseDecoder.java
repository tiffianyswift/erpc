package com.lavender.channel.handler;

import com.lavender.compress.Compressor;
import com.lavender.compress.CompressorFactory;
import com.lavender.serialiize.Serializer;
import com.lavender.serialiize.SerializerFactory;
import com.lavender.transport.enumeration.RequestType;
import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.ErpcRequestPayload;
import com.lavender.transport.message.ErpcResponse;
import com.lavender.transport.message.MessageConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 10:39
 **/

@Slf4j
public class ErpcResponseDecoder extends LengthFieldBasedFrameDecoder {
    public ErpcResponseDecoder() {
        super(
                MessageConstant.MAX_FRAME_LENGTH,
                MessageConstant.MAGIC_NUM.length + MessageConstant.VERSION_LENGTH + MessageConstant.HEADER_FIELD_LENGTH,
                MessageConstant.FULL_FIELD_LENGTH,
                -(MessageConstant.MAGIC_NUM.length + MessageConstant.VERSION_LENGTH + MessageConstant.HEADER_FIELD_LENGTH + MessageConstant.FULL_FIELD_LENGTH),
                0
        );
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if(decode instanceof ByteBuf byteBuf){
            return decodeFrame(byteBuf);
        }
        return null;
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        // parse magic num
        byte[] magicNum = new byte[MessageConstant.MAGIC_NUM.length];
        byteBuf.readBytes(magicNum);
        for (int i = 0; i < magicNum.length; i++) {
            if(magicNum[i] != MessageConstant.MAGIC_NUM[i]){
                throw new RuntimeException("获得的请求不合法。");
            }
        }
        // parse rpc protocol version
        byte version = byteBuf.readByte();
        if(version != MessageConstant.VERSION){
            throw new RuntimeException("请求的版本不被支持");
        }

        // parse head length
        short headLength = byteBuf.readShort();

        //parse full length
        int fullLength = byteBuf.readInt();

        //parse request type
        byte responseCode = byteBuf.readByte();

        //parse serialize type
        byte serializeType = byteBuf.readByte();

        //parse compress type
        byte compressType = byteBuf.readByte();

        // parse request id
        long responseId = byteBuf.readLong();

        long timeStamp = byteBuf.readLong();


        // todo 解压缩

        // todo 反序列化
        ErpcResponse erpcResponse =  ErpcResponse.builder()
                .code(responseCode)
                .compressType(compressType)
                .serializeType(serializeType)
                .responseId(responseId)
                .timeStamp(timeStamp)
                .build();


        int bodyLength = fullLength-headLength;
        byte[] payload = new byte[bodyLength];
        byteBuf.readBytes(payload);

        Compressor compressor = CompressorFactory.getCompressorWraper(compressType).getImpl();
        payload = compressor.decompress(payload);


        Serializer serializer = SerializerFactory.getSerializerWraper(erpcResponse.getSerializeType()).getImpl();
        Object body = serializer.deserialize(payload, Object.class);
        erpcResponse.setBody(body);

        if(log.isDebugEnabled()){
            log.debug("响应【{}】已完成报文的解码。", erpcResponse.getResponseId());
        }
        return erpcResponse;

    }
}
