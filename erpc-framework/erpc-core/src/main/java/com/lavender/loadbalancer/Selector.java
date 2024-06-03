package com.lavender.loadbalancer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-02 19:59
 **/

public interface Selector {
    InetSocketAddress getNext();
}
