package com.lavender;

import com.lavender.annotation.TryTimes;

public interface ExampleErpc {
    /**
     * example method
     * @param msg
     * @return
     */
    @TryTimes(tryTimes = 3, intervalTime = 3000)
    String saySo(String msg);
}
