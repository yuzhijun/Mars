package com.winning.mars_generator.core.modules.sm.blockCanary;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class ShortBlockBean {
    public long blockTime;

    public ShortBlockBean(long blockTime) {
        this.blockTime = blockTime;
    }

    @Override
    public String toString() {
        return "ShortBlockInfo{" +
                "blockTime=" + blockTime +
                '}';
    }
}
