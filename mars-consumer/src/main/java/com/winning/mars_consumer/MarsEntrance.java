package com.winning.mars_consumer;

import android.content.Context;

import com.winning.mars_consumer.utils.SPUtils;
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
}
