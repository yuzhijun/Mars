package com.winning.mars_generator.core.modules.sm.blockCanary;

import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.utils.BaseUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Millis
 * Created by yuzhijun on 2018/3/29.
 */
public class LongBlockBean {
    //block start time
    public long timeStart;
    //block end time
    public long timeEnd;
    //block time
    public long blockTime;
    /**
     * block thread cost time
     * if threadTimeCost == timeCost, show that task in this thread costs so mush time;
     * or threadTimeCost << timeCost, show that this thread is waiting for resource
     * */
    public long threadTimeCost;
    //memory detail info
    public MemoryBean mMemoryBean;
    // cpu is busy or not
    public boolean cpuBusy;
    //cpu detail info
    public List<CpuBean> cpuRateInfo = new ArrayList<>();
    //thread stack info(for String export)
    public Map<String, List<String>> threadStackEntriesForExport = new LinkedHashMap<>();
    //thread stack info
    public Map<Long, List<StackTraceElement>> mThreadStackEntries = new LinkedHashMap<>();

    public static LongBlockBean create(long realTimeStart, long realTimeEnd, long threadTime, long blockTime, boolean cpuBusy, List<CpuBean> crs, Map<Long, List<StackTraceElement>> ts, MemoryBean memoryInfo) {
        LongBlockBean blockBaseinfo = new LongBlockBean();
        blockBaseinfo.timeStart = realTimeStart;
        blockBaseinfo.timeEnd = realTimeEnd;
        blockBaseinfo.threadTimeCost = threadTime;
        blockBaseinfo.blockTime = blockTime;
        blockBaseinfo.cpuBusy = cpuBusy;
        blockBaseinfo.cpuRateInfo = crs;
        blockBaseinfo.mThreadStackEntries = ts;
        blockBaseinfo.threadStackEntriesForExport = BaseUtility.convertToStackString(blockBaseinfo.mThreadStackEntries);
        blockBaseinfo.mMemoryBean = memoryInfo;
        return blockBaseinfo;
    }

    @Override
    public String toString() {
        return "BlockBaseinfo{" +
                "timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", blockTime=" + blockTime +
                ", threadTimeCost=" + threadTimeCost +
                ", memoryDetailInfo=" + mMemoryBean +
                ", cpuBusy=" + cpuBusy +
                ", cpuRateInfos=" + cpuRateInfo +
                ", threadStackEntriesForExport=" + threadStackEntriesForExport +
                ", mThreadStackEntries=" + mThreadStackEntries +
                '}';
    }

    /**
     * same block shares the same block trace
     * calculate the hash code depend on the first block trace
     * if do not have stack trace, then return ""
     * @return String
     * */
    public String generateKey() {
        Iterator<Map.Entry<Long, List<StackTraceElement>>> iterator = mThreadStackEntries.entrySet().iterator();
        if (iterator.hasNext()) {
            return String.valueOf(iterator.next().getValue().hashCode());
        }
        return "";
    }
}
