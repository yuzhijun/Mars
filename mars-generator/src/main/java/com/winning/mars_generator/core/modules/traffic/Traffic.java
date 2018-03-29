package com.winning.mars_generator.core.modules.traffic;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * traffic
 * Created by yuzhijun on 2018/3/28.
 */
public class Traffic extends GeneratorSubject<TrafficBean> implements Install {
    private TrafficEngine mTrafficEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getTraffic());
    }

    private void install(Context context, MarsConfig.Traffic traffic){
        if (null != mTrafficEngine){
            LogUtil.d("Traffic module has already installed, skip install");
            return;
        }

        mTrafficEngine = new TrafficEngine(context,this,traffic.getIntervalMillis(),traffic.getSampleMillis());
        mTrafficEngine.launch();
        LogUtil.d("Traffic module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mTrafficEngine == null) {
            LogUtil.d("Traffic module has already uninstalled , skip uninstall.");
            return;
        }
        mTrafficEngine.stop();
        mTrafficEngine = null;
        LogUtil.d("Traffic module uninstalls successfully");
    }
}
