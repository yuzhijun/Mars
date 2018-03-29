package com.winning.mars_generator.core.modules.memory;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class HeapBean {
    public long freeMemKb;
    public long maxMemKb;
    public long allocatedKb;

    @Override
    public String toString() {
        return "HeapInfo{" +
                "freeMemKb=" + freeMemKb +
                ", maxMemKb=" + maxMemKb +
                ", allocatedKb=" + allocatedKb +
                '}';
    }
}
