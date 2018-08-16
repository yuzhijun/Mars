package com.winning.mars_generator.core.modules.sm.blockCanary;

import android.os.SystemClock;
import android.util.Printer;

public class LooperMonitor implements Printer {
    public static final String TAG = "LooperMonitor";
    //long block threshold
    private long mLongBlockThresholdMillis;
    // short block threshold
    private long mShortBlockThresholdMillis;
    // this event start time
    private long mThisEventStartTime = 0;
    // this event start time(in thread)
    private long mThisEventStartThreadTime = 0;
    private BlockListener mBlockListener = null;
    //event start flag
    private boolean mEventStart = false;

    public interface BlockListener {
        void onEventStart(long startTime);

        void onEventEnd(long endTime);

        /**
         * block event
         * @param eventStartTimeMilliis     event start time
         * @param eventEndTimeMillis        event end time
         * @param blockTimeMillis           block time(event solve time)
         * @param threadBlockTimeMillis     actual thread block time
         * @param longBlockThresholdMillis  long block threshold
         * @param shortBlockThresholdMillis short block threshold
         */
        void onBlockEvent(long blockTimeMillis, long threadBlockTimeMillis, boolean longBlock,
                          long eventStartTimeMilliis, long eventEndTimeMillis, long longBlockThresholdMillis,
                          long shortBlockThresholdMillis);
    }

    public LooperMonitor(BlockListener blockListener, long longBlockThresholdMillis, long shortBlockThresholdMillis) {
        if (blockListener == null) {
            throw new IllegalArgumentException("blockListener should not be null.");
        }
        mBlockListener = blockListener;
        mLongBlockThresholdMillis = longBlockThresholdMillis;
        mShortBlockThresholdMillis = shortBlockThresholdMillis;
    }

    /**
     * update block threshold
     * @param shortBlockThresholdMillis
     * @param longBlockThresholdMillis
     */
    public void setBlockThreshold(long shortBlockThresholdMillis, long longBlockThresholdMillis) {
        this.mShortBlockThresholdMillis = shortBlockThresholdMillis;
        this.mLongBlockThresholdMillis = longBlockThresholdMillis;
    }

    @Override
    public void println(String x) {
        if (!mEventStart) {// event start
            mThisEventStartTime = System.currentTimeMillis();
            mThisEventStartThreadTime = SystemClock.currentThreadTimeMillis();
            mEventStart = true;
            mBlockListener.onEventStart(mThisEventStartTime);
        } else {// event end
            final long thisEventEndTime = System.currentTimeMillis();
            final long thisEventThreadEndTime = SystemClock.currentThreadTimeMillis();
            mEventStart = false;

            long eventCostTime = thisEventEndTime - mThisEventStartTime;
            long eventCostThreadTime = thisEventThreadEndTime - mThisEventStartThreadTime;
            if (eventCostTime >= mLongBlockThresholdMillis) {
                mBlockListener.onBlockEvent(eventCostTime, eventCostThreadTime, true, mThisEventStartTime,
                        thisEventEndTime, mLongBlockThresholdMillis, mShortBlockThresholdMillis);
            } else if (eventCostTime >= mShortBlockThresholdMillis) {
                mBlockListener.onBlockEvent(eventCostTime, eventCostThreadTime, false, mThisEventStartTime,
                        thisEventEndTime, mLongBlockThresholdMillis, mShortBlockThresholdMillis);
            }
            mBlockListener.onEventEnd(thisEventEndTime);
        }
    }
}