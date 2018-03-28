package com.winning.mars_generator.core.modules.device;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class Device extends GeneratorSubject<DeviceBean> implements Install<MarsConfig.BaseConfig> {

    private DeviceEngine mDeviceEngine;

    @Override
    public void install(Context context) {
        if (null != mDeviceEngine){
            LogUtil.d("Device module has already installed, skip install");
            return;
        }

        mDeviceEngine = new DeviceEngine(this,context);
        mDeviceEngine.launch();
        LogUtil.d("Device module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mDeviceEngine == null) {
            LogUtil.d("Device module has already uninstalled , skip uninstall.");
            return;
        }
        mDeviceEngine.stop();
        mDeviceEngine = null;
        LogUtil.d("Device module uninstalls successfully");
    }
}
