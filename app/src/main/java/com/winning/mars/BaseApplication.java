package com.winning.mars;

import android.app.Application;

import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_consumer.utils.StartupTracer;

/**
 * Created by yuzhijun on 2018/3/27.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MarsEntrance.getInstance().init(this,"1233kkhr33ll333","ddeefw");
        StartupTracer.get().onApplicationCreate();
    }
}
