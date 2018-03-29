package com.winning.mars_generator.core.modules.battery;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class Battery extends GeneratorSubject<BatteryBean> implements Install {
    private BatteryEngine mBatteryEngine;

    @Override
    public void install(Context context) {
        install(context, MarsConfig.getBattery());
    }

    private void install(Context context, MarsConfig.Battery battery){
        if (mBatteryEngine != null) {
            LogUtil.d("Battery module has already installed, skip install");
            return;
        }
        mBatteryEngine = new BatteryEngine(this,context, battery.getIntervalMillis());
        mBatteryEngine.launch();
        LogUtil.d("Battery module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mBatteryEngine == null) {
            LogUtil.d("Battery module has already uninstalled , skip uninstall.");
            return;
        }
        mBatteryEngine.stop();
        mBatteryEngine = null;
        LogUtil.d("Battery module uninstalls successfully");
    }
}
