package com.winning.mars_consumer.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.startup.Startup;
import com.winning.mars_generator.core.modules.startup.StartupBean;

public class StartupTracer {
    public interface OnStartupEndCallback {
        public void onStartupEnd();
    }

    private long mApplicationStartTime;
    private long mSplashStartTime;

    private StartupTracer() {
    }

    private static class InstanceHolder {
        private static final StartupTracer sInstance = new StartupTracer();
    }

    public static StartupTracer get() {
        return InstanceHolder.sInstance;
    }

    public void onApplicationCreate() {
        mApplicationStartTime = System.currentTimeMillis();
    }

    public void onSplashCreate() {
        mSplashStartTime = System.currentTimeMillis();
    }

    public void onHomeCreate(Activity activity) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("call onHomeCreate ui thread!");
        }
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Startup startup = Mars.getInstance(activity).getModule(Startup.class);
                        startup.generate(generateStartupInfo(System.currentTimeMillis()));
                    }
                });
            }
        });
    }

    private StartupBean generateStartupInfo(long homeEndTime) {
        if (isCodeStart(homeEndTime)) {
            return new StartupBean(StartupBean.StartUpType.COLD, homeEndTime - mApplicationStartTime);
        } else if (isHotStart(homeEndTime)) {
            return new StartupBean(StartupBean.StartUpType.HOT, homeEndTime - mSplashStartTime);
        } else {
            return null;
        }
    }

    private boolean isCodeStart(long homeEndTime) {
        return mApplicationStartTime > 0 && homeEndTime > mSplashStartTime && mSplashStartTime > mApplicationStartTime;
    }

    private boolean isHotStart(long homeEndTime) {
        return mApplicationStartTime <= 0 && mSplashStartTime > 0 && homeEndTime > mSplashStartTime;
    }
}
