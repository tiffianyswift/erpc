package com.lavender.channel.handler;

import com.lavender.transport.message.ErpcRequest;
import com.lavender.transport.message.ErpcRequestPayload;
import com.lavender.transport.message.MessageConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 08:45
 **/
@Slf4j
public class ErpcRequestEncoder extends MessageToByteEncoder<ErpcRequest> {
    /**
     * 4B magic num
     * 1B protocol version
     * 2B header length
     * 4B message length
     * 1B serialize type
     * 1B compress type
     * 1B request type
     * 8B request Id
     * @param channelHandlerContext
     * @param erpcRequest
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ErpcRequest erpcRequest, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(MessageConstant.MAGIC_NUM);
        byteBuf.writeByte(MessageConstant.VERSION);
        byteBuf.writeShort(MessageConstant.HEADER_LENGTH);

        byteBuf.writerIndex(byteBuf.writerIndex()+MessageConstant.FULL_FIELD_LENGTH);

        byteBuf.writeByte(erpcRequest.getRequestType());
        byteBuf.writeByte(erpcRequest.getSerializeType());
        byteBuf.writeByte(erpcRequest.getCompressType());
        byteBuf.writeLong(erpcRequest.getRequestId());


        byte[] bodyBytes = getBodyBytes(erpcRequest.getRequestPayload());
        byteBuf.writeBytes(bodyBytes);

        int writerIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(MessageConstant.MAGIC_NUM.length + MessageConstant.VERSION_LENGTH + MessageConstant.HEADER_FIELD_LENGTH);
        byteBuf.writeInt(MessageConstant.HEADER_LENGTH + bodyBytes.length);

        byteBuf.writerIndex(writerIndex);
        if(log.isDebugEnabled()){
            log.debug("请求【{}】已完成报文的编码。", erpcRequest.getRequestId());
        }
    }
    private byte[] getBodyBytes(ErpcRequestPayload requestPayload){
        if(requestPayload==null){
            return new byte[0];
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(requestPayload);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("序列化时出现异常.");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        byte[] b = new byte[0];
        System.out.println(b.length);
    }
}
