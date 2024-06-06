package com.lavender.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-06 14:20
 **/

public class ShutdownHolder {
    public static AtomicBoolean BAFFLE = new AtomicBoolean(false);

    public static LongAdder REQUEST_COUNTER = new LongAdder();
}
