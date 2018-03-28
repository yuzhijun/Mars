package com.winning.mars;

import android.app.Application;

import com.winning.mars_generator.Mars;

/**
 * Created by yuzhijun on 2018/3/27.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Mars.getInstance(this);
    }
}
