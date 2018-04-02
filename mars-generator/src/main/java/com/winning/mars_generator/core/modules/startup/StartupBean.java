package com.winning.mars_generator.core.modules.startup;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yuzhijun on 2018/4/2.
 */

public class StartupBean {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({StartUpType.COLD, StartUpType.HOT})
    public @interface StartUpType {
        public static final String COLD = "cold";
        public static final String HOT = "hot";
    }

    public @StartUpType
    String startupType;
    public long startupTime;

    public StartupBean(String startupType, long startupTime) {
        this.startupType = startupType;
        this.startupTime = startupTime;
    }

    @Override
    public String toString() {
        return "StartupInfo{" +
                "startupType='" + startupType + '\'' +
                ", startupTime=" + startupTime +
                '}';
    }
}
