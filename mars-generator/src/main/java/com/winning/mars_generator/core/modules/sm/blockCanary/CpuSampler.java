package com.winning.mars_generator.core.modules.sm.blockCanary;

import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.utils.IoUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * cpu used rate
 * Created by yuzhijun on 2018/3/29.
 */
public class CpuSampler extends AbstractSampler {

    private static final String TAG = "CpuSampler";
    private static final int BUFFER_SIZE = 1000;

    private final int BUSY_TIME;
    private static final int MAX_ENTRY_COUNT = 10;

    private final LinkedHashMap<Long, CpuBean> mCpuInfoEntries = new LinkedHashMap<>();
    private int mPid = 0;
    private long mUserLast = 0;
    private long mSystemLast = 0;
    private long mIdleLast = 0;
    private long mIoWaitLast = 0;
    private long mTotalLast = 0;
    private long mAppCpuTimeLast = 0;

    public CpuSampler(long sampleInterval) {
        super(sampleInterval);
        BUSY_TIME = (int) (mSampleInterval * 1.2f);
    }

    @Override
    public void start() {
        reset();
        super.start();
    }

    /**
     * get cpu used info this period
     * @return string show cpu rate information
     */
    public List<CpuBean> getCpuRateInfo(long startTime, long endTime) {
        List<CpuBean> result = new ArrayList<>();
        synchronized (mCpuInfoEntries) {
            for (Map.Entry<Long, CpuBean> entry : mCpuInfoEntries.entrySet()) {
                if (startTime < entry.getKey() && entry.getKey() < endTime) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * is cpu busy
     * if mSampleInterval > mSampleInterval*1.2 ,return true
     * @param start
     * @param end
     * @return
     */
    public boolean isCpuBusy(long start, long end) {
        if (end - start > mSampleInterval) {
            long s = start - mSampleInterval;
            long e = start + mSampleInterval;
            long last = 0;
            synchronized (mCpuInfoEntries) {
                for (Map.Entry<Long, CpuBean> entry : mCpuInfoEntries.entrySet()) {
                    long time = entry.getKey();
                    if (s < time && time < e) {
                        if (last != 0 && time - last > BUSY_TIME) {
                            return true;
                        }
                        last = time;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void doSample() {
        /**
         * cpu sample should do twice, or else is null
         * for example，if block time is 1000ms,sample delay is 800ms，sample interval is 300ms，in runtime
         * block time > 1000ms && < 1400ms （delayTime + 2*intervalMillis），can not get cpu info
         */
        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;
        try {
            cpuReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), BUFFER_SIZE);
            String cpuRate = cpuReader.readLine();
            if (cpuRate == null) {
                cpuRate = "";
            }

            if (mPid == 0) {
                mPid = android.os.Process.myPid();
            }
            pidReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + mPid + "/stat")), BUFFER_SIZE);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null) {
                pidCpuRate = "";
            }

            parse(cpuRate, pidCpuRate);
        } catch (Throwable throwable) {
        } finally {
            IoUtil.closeSilently(cpuReader);
            IoUtil.closeSilently(pidReader);
        }
    }

    private void reset() {
        mUserLast = 0;
        mSystemLast = 0;
        mIdleLast = 0;
        mIoWaitLast = 0;
        mTotalLast = 0;
        mAppCpuTimeLast = 0;
    }

    private void parse(String cpuRate, String pidCpuRate) {
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length < 9) {
            return;
        }
        long user = Long.parseLong(cpuInfoArray[2]);
        long nice = Long.parseLong(cpuInfoArray[3]);
        long system = Long.parseLong(cpuInfoArray[4]);
        long idle = Long.parseLong(cpuInfoArray[5]);
        long ioWait = Long.parseLong(cpuInfoArray[6]);
        long total = user + nice + system + idle + ioWait
                + Long.parseLong(cpuInfoArray[7])
                + Long.parseLong(cpuInfoArray[8]);
        String[] pidCpuInfoList = pidCpuRate.split(" ");
        if (pidCpuInfoList.length < 17) {
            return;
        }

        long appCpuTime = Long.parseLong(pidCpuInfoList[13])
                + Long.parseLong(pidCpuInfoList[14])
                + Long.parseLong(pidCpuInfoList[15])
                + Long.parseLong(pidCpuInfoList[16]);
        if (mTotalLast != 0) {
            long idleTime = idle - mIdleLast;
            long totalTime = total - mTotalLast;
            CpuBean cpuInfo = new CpuBean((totalTime - idleTime) * 100L / totalTime, (appCpuTime - mAppCpuTimeLast) *
                    100L / totalTime,
                    (user - mUserLast) * 100L / totalTime, (system - mSystemLast) * 100L / totalTime, (ioWait -
                    mIoWaitLast) * 100L / totalTime);

            synchronized (mCpuInfoEntries) {
                mCpuInfoEntries.put(System.currentTimeMillis(), cpuInfo);
                if (mCpuInfoEntries.size() > MAX_ENTRY_COUNT) {
                    for (Map.Entry<Long, CpuBean> entry : mCpuInfoEntries.entrySet()) {
                        Long key = entry.getKey();
                        mCpuInfoEntries.remove(key);
                        break;
                    }
                }
            }
        }
        mUserLast = user;
        mSystemLast = system;
        mIdleLast = idle;
        mIoWaitLast = ioWait;
        mTotalLast = total;

        mAppCpuTimeLast = appCpuTime;
    }
}
