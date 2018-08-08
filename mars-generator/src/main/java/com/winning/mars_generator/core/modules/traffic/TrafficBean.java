package com.winning.mars_generator.core.modules.traffic;

import com.winning.mars_generator.core.BaseBean;

import java.util.Locale;

/**
 * used traffic ,KB
 * Created by yuzhijun on 2018/3/29.
 */
public class TrafficBean extends BaseBean{
    // total download traffic rate
    public float rxTotalRate;
    // total upload traffic rate
    public float txTotalRate;
    // app download traffic rate
    public float rxUidRate;
    // app upload traffic rate
    public float txUidRate;

    public long sampleTime;

    public TrafficBean(float rxTotalRate, float txTotalRate, float rxUidRate, float txUidRate, long sampleTime) {
        this.rxTotalRate = rxTotalRate;
        this.txTotalRate = txTotalRate;
        this.rxUidRate = rxUidRate;
        this.txUidRate = txUidRate;
        this.sampleTime = sampleTime;
    }

    public TrafficBean() {
    }

    @Override
    public String toString() {
        return "rxUidRate=" + String.format(Locale.US, "%.3f kb/s", rxUidRate) +
                ", txUidRate=" + String.format(Locale.US, "%.3f kb/s", txUidRate) +
                ", rxTotalRate=" + String.format(Locale.US, "%.3f kb/s", rxTotalRate) +
                ", txTotalRate=" + String.format(Locale.US, "%.3f kb/s", txTotalRate);
    }
}
