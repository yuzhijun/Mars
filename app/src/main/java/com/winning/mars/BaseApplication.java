package com.winning.mars;

import android.app.Application;
import android.widget.Toast;

import com.winning.mars_consumer.ICustomForbiddenBehavior;
import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_consumer.utils.StartupTracer;

/**
 * Created by yuzhijun on 2018/3/27.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MarsEntrance.getInstance().init(this, "1233kkhr33ll333", new ICustomForbiddenBehavior() {
            @Override
            public void onForbiddenBehavior() {
                Toast.makeText(getApplicationContext(), "我是自定义的禁用行为", Toast.LENGTH_SHORT).show();
            }
        });
//        MarsEntrance.getInstance().init(this, "1233kkhr33ll333", null);
        StartupTracer.get().onApplicationCreate();
    }
}
