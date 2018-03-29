package com.winning.mars_generator.core.modules.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

import com.winning.mars_generator.utils.ProcessUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by yuzhijun on 2018/3/29.
 */
public class MemoryUtil {
    /**
     * get app dalvik memory info
     * ignore time cost
     * @return dalvik heap KB
     */
    public static HeapBean getAppHeapInfo() {
        Runtime runtime = Runtime.getRuntime();
        HeapBean heapInfo = new HeapBean();
        heapInfo.freeMemKb = runtime.freeMemory() / 1024;
        heapInfo.maxMemKb = Runtime.getRuntime().maxMemory() / 1024;
        heapInfo.allocatedKb = (Runtime.getRuntime().totalMemory() - runtime.freeMemory()) / 1024;
        return heapInfo;
    }

    /**
     * get pss info
     * @param context
     * @return pss KB
     */
    public static PssBean getAppPssInfo(Context context) {
        final int pid = ProcessUtils.getCurrentPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{pid})[0];
        PssBean pssInfo = new PssBean();
        pssInfo.totalPssKb = memoryInfo.getTotalPss();
        pssInfo.dalvikPssKb = memoryInfo.dalvikPss;
        pssInfo.nativePssKb = memoryInfo.nativePss;
        pssInfo.otherPssKb = memoryInfo.otherPss;
        return pssInfo;
    }

    public static RamBean getRamInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        final RamBean ramMemoryInfo = new RamBean();
        ramMemoryInfo.availMemKb = mi.availMem / 1024;
        ramMemoryInfo.isLowMemory = mi.lowMemory;
        ramMemoryInfo.lowMemThresholdKb = mi.threshold / 1024;
        ramMemoryInfo.totalMemKb = getRamTotalMem(am);
        return ramMemoryInfo;
    }

    /**
     * get total system ram
     * @param activityManager
     * @return
     */
    private static long getRamTotalMem(ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);
            return mi.totalMem / 1024;
        } else if (sTotalMem.get() > 0L) {
            return sTotalMem.get();
        } else {
            final long tm = getRamTotalMemByFile();
            sTotalMem.set(tm);
            return tm;
        }
    }

    private static AtomicLong sTotalMem = new AtomicLong(0L);

    /**
     * get Ram scale，the same to activityManager.getMemoryInfo(mi).totalMem,
     * for example,when Sdk > API16 we can use api to get ram scale
     * or else read file ["/proc/meminfo"]
     * @return scale KB
     */
    private static long getRamTotalMemByFile() {
        final String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine
                    .indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
                    "\\D+", ""));
            return totalMemorySize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
