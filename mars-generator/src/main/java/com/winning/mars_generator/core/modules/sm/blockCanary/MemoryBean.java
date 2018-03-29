package com.winning.mars_generator.core.modules.sm.blockCanary;

import com.winning.mars_generator.core.modules.memory.HeapBean;
import com.winning.mars_generator.core.modules.memory.PssBean;
import com.winning.mars_generator.core.modules.memory.RamBean;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class MemoryBean {
    public HeapBean mHeapBean;
    public PssBean mPssBean;
    public RamBean mRamBean;

    public MemoryBean(HeapBean heapBean, PssBean pssBean, RamBean ramBean) {
        this.mHeapBean = heapBean;
        this.mPssBean = pssBean;
        this.mRamBean = ramBean;
    }

    @Override
    public String toString() {
        return "MemoryInfo{" +
                "heapInfo=" + mHeapBean +
                ", pssInfo=" + mPssBean +
                ", ramInfo=" + mRamBean +
                '}';
    }
}
