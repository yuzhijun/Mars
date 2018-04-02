package com.winning.mars_consumer.monitor.uploader;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class WakeCPUReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent wakefulIntent = new Intent(context, WorkService.class);
        startWakefulService(context, wakefulIntent);
    }
}
