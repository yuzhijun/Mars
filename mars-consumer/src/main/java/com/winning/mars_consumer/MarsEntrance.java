package com.winning.mars_consumer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.winning.mars_consumer.utils.SPUtils;
import com.winning.mars_consumer.utils.UsableCheckUtil;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.battery.Battery;
import com.winning.mars_generator.core.modules.cpu.Cpu;
import com.winning.mars_generator.core.modules.crash.Crash;
import com.winning.mars_generator.core.modules.device.Device;
import com.winning.mars_generator.core.modules.fps.Fps;
import com.winning.mars_generator.core.modules.inflate.Inflate;
import com.winning.mars_generator.core.modules.leak.Leak;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.LeakCanary;
import com.winning.mars_generator.core.modules.sm.Sm;
import com.winning.mars_generator.core.modules.thread.deadlock.DeadLock;
import com.winning.mars_generator.core.modules.traffic.Traffic;

/**
 * Created by yuzhijun on 2018/4/10.
 */

public class MarsEntrance {
    private static MarsEntrance mInstance;
    public  String appKey;
    private int mFinalCount;
    private static boolean checkUsableFlag = true;
    public ICustomForbiddenBehavior customForbiddenBehavior;

    private MarsEntrance(){
    }

    public static MarsEntrance getInstance(){
        if (null == mInstance){
            synchronized (MarsEntrance.class){
                if (null == mInstance){
                    mInstance = new MarsEntrance();
                }
            }
        }
        return mInstance;
    }

    /**
     * init
     * @Param key appKey
     * */
    public void init(Context context, String appKey,ICustomForbiddenBehavior customForbiddenBehavior){
        Mars.getInstance(context).install(Leak.class);

        if (!LeakCanary.isInAnalyzerProcess(context)){
            this.appKey = appKey;
            this.customForbiddenBehavior = customForbiddenBehavior;
            SPUtils.init(context);
            checkUsable(context);
            Mars.getInstance(context).install(Cpu.class)
                    .install(Battery.class)
                    .install(Crash.class)
                    .install(Fps.class)
                    .install(Inflate.class)
                    .install(Device.class)
                    .install(Sm.class)
                    .install(DeadLock.class)
                    .install(Traffic.class);

            MarsConsumer.consume(context);
        }
    }

    public void stop(){
        Mars.getInstance(MarsConsumer.mContext).getModule(Battery.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Cpu.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Crash.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Fps.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Leak.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Sm.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(DeadLock.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Traffic.class).uninstall();

        MarsConsumer.stop();
    }

    public void checkUsable(Context context){
        ((Application)context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mFinalCount++;
                if (mFinalCount == 2){
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    activity.getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    InnerCheckUsable(context);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                if (mFinalCount == 0){

                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private void InnerCheckUsable(Context context) {
        if (isCheckUsableFlag()){
            UsableCheckUtil.checkUsable(context);
        }
    }

    public static void setCheckUsableFlag(boolean checkUsableFlag) {
        MarsEntrance.checkUsableFlag = checkUsableFlag;
    }

    public static boolean isCheckUsableFlag() {
        return checkUsableFlag;
    }
}
