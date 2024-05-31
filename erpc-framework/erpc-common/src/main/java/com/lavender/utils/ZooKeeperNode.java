package com.lavender.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 12:39
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZooKeeperNode {
    private String nodePath;
    private byte[] data;
}
