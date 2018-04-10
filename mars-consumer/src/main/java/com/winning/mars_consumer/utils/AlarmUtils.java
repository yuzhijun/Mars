package com.winning.mars_consumer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class AlarmUtils {

    public static void setRTCWakeup(AlarmManager alarmManager, int defaultTriggerAtMillis, PendingIntent pendingIntent) {
        long currentTimeMillis = System.currentTimeMillis();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,currentTimeMillis, defaultTriggerAtMillis,pendingIntent);
    }

    public static void setElapsedWakeup(AlarmManager alarmManager, int defaultTriggerAtMillis, PendingIntent pendingIntent) {
        long firstTime = SystemClock.elapsedRealtime();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, defaultTriggerAtMillis,pendingIntent);
    }
}
