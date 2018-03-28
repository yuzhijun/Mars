package com.winning.mars_generator.core.modules.cpu;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.winning.mars_generator.exception.MarsInvalidDataException;
import com.winning.mars_generator.utils.IoUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


/**
 * 1. adb shell dumpsys cpuinfo |grep packagename
 * 2. adb shell top -m 10 -s cpu
 *  Refer to [https://www.jianshu.com/p/66368d401c51] for more information
 *  Created by yuzhijun on 2018/3/27.
 */
public class CpuSnapshot {
    public long user = 0;
    public long system = 0;
    public long idle = 0;
    public long ioWait = 0;
    public long total = 0;
    public long app = 0;

    public CpuSnapshot(long user, long system, long idle, long ioWait, long total, long app) {
        this.user = user;
        this.system = system;
        this.idle = idle;
        this.ioWait = ioWait;
        this.total = total;
        this.app = app;
    }
    public CpuSnapshot() {
    }
    private static final int BUFFER_SIZE = 1024;

    /**
     * cpu snapshot
     * @return invalid
     */
    @WorkerThread
    public synchronized static @NonNull CpuSnapshot snapshot() {
        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;
        try {
            //system cpu time-slicing
            cpuReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), BUFFER_SIZE);
            String cpuRate = cpuReader.readLine();
            if (cpuRate == null) {
                cpuRate = "";
            }
            //process and thread cpu time-slicing
            int pid = android.os.Process.myPid();
            pidReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), BUFFER_SIZE);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null) {
                pidCpuRate = "";
            }
            //from system started, cpu snapshot
            return parse(cpuRate, pidCpuRate);
        } catch (Throwable throwable) {
            throw new MarsInvalidDataException(throwable);
        } finally {
            IoUtil.closeSilently(cpuReader);
            IoUtil.closeSilently(pidReader);
        }
    }

    private static CpuSnapshot parse(String cpuRate, String pidCpuRate) throws Throwable {
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length < 9) {
            throw new IllegalStateException("Cpu info array size must greater than 9");
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
            throw new IllegalStateException("Pid cpu info array size must greater than 17");
        }
        long appCpuTime = Long.parseLong(pidCpuInfoList[13])
                + Long.parseLong(pidCpuInfoList[14])
                + Long.parseLong(pidCpuInfoList[15])
                + Long.parseLong(pidCpuInfoList[16]);
        return new CpuSnapshot(user, system, idle, ioWait, total, appCpuTime);
    }
}
