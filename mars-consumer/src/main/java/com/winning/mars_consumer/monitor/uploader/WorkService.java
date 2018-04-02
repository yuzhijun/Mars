package com.winning.mars_consumer.monitor.uploader;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class WorkService extends IntentService {
    public WorkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO Time consuming task

        WakeCPUReceiver.completeWakefulIntent(intent);
    }
}
