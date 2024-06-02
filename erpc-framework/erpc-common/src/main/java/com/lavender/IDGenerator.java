package com.lavender;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author: lavender
 * @Desc: generate id of request
 * @create: 2024-06-01 17:03
 **/

public class IDGenerator {
    public static final long START_STAMP = DateUtil.get("2022-1-1").getTime();
    public static final long DATA_CENTER_BIT = 5L;
    public static final long MACHINE_BIT = 5L;
    public static final long SEQUENCE_BIT = 12L;

    public static final long DATA_CENTER_MAX = ~(-1L << DATA_CENTER_BIT);
    public static final long MACHINE_MAX = ~(-1L << MACHINE_BIT);
    public static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    // timestamp -> datacenter -> machine -> sequence

    public static final long TIMESTAMP_LEFT = DATA_CENTER_BIT + MACHINE_BIT + SEQUENCE_BIT;
    public static final long DATA_CENTER_LEFT = MACHINE_BIT + SEQUENCE_BIT;
    public static final long MACHINE_LEFT = SEQUENCE_BIT;

    public long dataCenterId;
    private long machineId;
    private LongAdder sequenceId = new LongAdder();

    private long lastTimeStamp = -1;

    public IDGenerator(long dataCenterId, long machineId) {
        if(dataCenterId > DATA_CENTER_MAX || machineId > MACHINE_MAX){
            throw new IllegalArgumentException("传入的数据中心编号或者机器编号不合法");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public long getId(){
        long currentTime = System.currentTimeMillis();
        long timeStamp = currentTime - START_STAMP;
        if(timeStamp < lastTimeStamp){
            throw new RuntimeException("产生时钟回拨");
        }
        if(timeStamp == lastTimeStamp){
            sequenceId.increment();
            if(sequenceId.sum() >= SEQUENCE_MAX){
                while(System.currentTimeMillis() - START_STAMP == timeStamp);
                
            }
        }
        else{
            sequenceId.reset();
        }
        lastTimeStamp = timeStamp;
        long sequence = sequenceId.sum();
        return (timeStamp << TIMESTAMP_LEFT) | (dataCenterId << DATA_CENTER_LEFT) | (machineId << MACHINE_LEFT) | sequence;
    }

    public static void main(String[] args) {
        IDGenerator idGenerator = new IDGenerator(1, 2);
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> System.out.println(idGenerator.getId())).start();
        }
    }

}
