package com.winning.mars_generator.core.modules.traffic;

import android.net.TrafficStats;
import android.support.annotation.WorkerThread;

/**
 * traffic snap shot
 * Created by yuzhijun on 2018/3/29.
 */
public class TrafficSnapshot {
    //total download KB
    public float rxTotalKB;
    //total upload KB
    public float txTotalKB;
    //app download KB
    public float rxUidKB;
    //app upload KB
    public float txUidKB;

    @WorkerThread
    public static TrafficSnapshot snapshot() {
        TrafficSnapshot snapshot = new TrafficSnapshot();
        snapshot.rxTotalKB = TrafficStats.getTotalRxBytes() / 1024f;
        snapshot.txTotalKB = TrafficStats.getTotalTxBytes() / 1024f;
        snapshot.rxUidKB = TrafficStats.getUidRxBytes(android.os.Process.myUid()) / 1024f;
        snapshot.txUidKB = TrafficStats.getUidTxBytes(android.os.Process.myUid()) / 1024f;
        return snapshot;
    }
}
