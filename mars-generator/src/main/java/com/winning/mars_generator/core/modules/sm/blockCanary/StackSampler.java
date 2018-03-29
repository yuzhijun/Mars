package com.winning.mars_generator.core.modules.sm.blockCanary;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class StackSampler extends AbstractSampler {

    private static final int DEFAULT_MAX_ENTRY_COUNT = 30;
    private static final LinkedHashMap<Long, StackTraceElement[]> sStackMap = new LinkedHashMap<>();

    private int mMaxEntryCount = DEFAULT_MAX_ENTRY_COUNT;
    private Thread mCurrentThread;

    public StackSampler(Thread thread, long sampleIntervalMillis) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis);
    }

    public StackSampler(Thread thread, int maxEntryCount, long sampleIntervalMillis) {
        super(sampleIntervalMillis);
        mCurrentThread = thread;
        mMaxEntryCount = maxEntryCount;
    }

    /**
     * obtain dump stack trace this period
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<Long, List<StackTraceElement>> getThreadStackEntries(long startTime, long endTime) {
        Map<Long, List<StackTraceElement>> result = new LinkedHashMap<>();
        synchronized (sStackMap) {
            for (Long entryTime : sStackMap.keySet()) {
                if (startTime < entryTime && entryTime < endTime) {
                    result.put(entryTime, Arrays.asList(sStackMap.get(entryTime)));
                }
            }
        }
        return result;
    }

    @Override
    protected void doSample() {
        synchronized (sStackMap) {
            if (sStackMap.size() == mMaxEntryCount && mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keySet().iterator().next());
            }
            sStackMap.put(System.currentTimeMillis(), mCurrentThread.getStackTrace());
        }
    }
}
