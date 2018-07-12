package com.winning.mars_generator.core.modules.cpu;

import com.winning.mars_generator.core.BaseBean;

import java.util.Locale;

/**
 * cpu ratio
 * Created by yuzhijun on 2018/3/27.
 */
public class CpuBean extends BaseBean{
    //total used ratio(user + system + io + others)
    public double totalUseRatio;
    // app's cpu ratio
    public double appCpuRatio;
    // user process's cpu ratio
    public double userCpuRatio;
    // system process's cpu ratio
    public double sysCpuRatio;
    // io waiting ratio
    public double ioWaitRatio;

    public String sampleTime;

    public CpuBean(double totalUseRatio, double appCpuRatio, double userCpuRatio, double sysCpuRatio, double
            ioWaitRatio, String sampleTime) {
        this.totalUseRatio = totalUseRatio;
        this.appCpuRatio = appCpuRatio;
        this.userCpuRatio = userCpuRatio;
        this.sysCpuRatio = sysCpuRatio;
        this.ioWaitRatio = ioWaitRatio;
        this.sampleTime = sampleTime;
    }

    public CpuBean() {
    }

    @Override
    public String toString() {
        return "app:" +
                String.format(Locale.US, "%.1f", appCpuRatio * 100f) +
                "% , total:" +
                String.format(Locale.US, "%.1f", totalUseRatio * 100f) +
                "% , user:" +
                String.format(Locale.US, "%.1f", userCpuRatio * 100f) +
                "% , system:" +
                String.format(Locale.US, "%.1f", sysCpuRatio * 100f) +
                "% , iowait:" +
                String.format(Locale.US, "%.1f", ioWaitRatio * 100f) +
                "% , sampleTime:" +
                sampleTime;
    }
}
