package com.winning.mars_generator.core.modules.memory;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class RamBean {
    public long availMemKb;
    public long totalMemKb;
    public long lowMemThresholdKb;
    public boolean isLowMemory;

    @Override
    public String toString() {
        return "RamMemoryInfo{" +
                "availMem=" + availMemKb +
                ", totalMem=" + totalMemKb +
                ", lowMemThreshold=" + lowMemThresholdKb +
                ", isLowMemory=" + isLowMemory +
                '}';
    }
}
