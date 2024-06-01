package com.lavender.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 07:21
 **/

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErpcRequestPayload implements Serializable{
    private String interfaceName;
    private String methodName;
    private Class<?>[] parametersType;
    private Object[] parametersValue;
    private Class<?> returnType;

}
