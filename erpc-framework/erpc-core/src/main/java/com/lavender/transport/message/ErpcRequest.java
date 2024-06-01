package com.lavender.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 07:20
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErpcRequest {
    private long requestId;

    private byte requestType;
    private byte compressType;
    private byte serializeType;

    ErpcRequestPayload requestPayload;
}
