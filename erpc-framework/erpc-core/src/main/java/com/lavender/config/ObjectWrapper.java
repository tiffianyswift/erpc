package com.lavender.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 12:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectWrapper<T> {
    private Byte code;
    private String name;
    private T impl;
}
