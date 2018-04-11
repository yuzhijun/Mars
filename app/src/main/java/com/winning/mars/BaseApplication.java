package com.winning.mars;

import android.app.Application;

import com.antfortune.freeline.FreelineCore;
import com.winning.mars_consumer.MarsEntrance;

/**
 * Created by yuzhijun on 2018/3/27.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        FreelineCore.init(this);
        super.onCreate();
        MarsEntrance.getInstance().init(this,"1233kkhr33ll333");
    }
}
