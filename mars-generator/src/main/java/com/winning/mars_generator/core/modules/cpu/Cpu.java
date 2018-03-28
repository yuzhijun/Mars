package com.winning.mars_generator.core.modules.cpu;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public class Cpu extends GeneratorSubject<CpuBean> implements Install<MarsConfig.CPU>{
    private CpuEngine mCpuEngine;

    @Override
    public synchronized void install(Context context) {
        install(MarsConfig.getCpu());
    }

    public void install(MarsConfig.CPU config) {
        if (null != mCpuEngine){
            LogUtil.d("Cpu module has already installed, skip install");
            return;
        }

        mCpuEngine = new CpuEngine(this,config.getIntervalMillis(),config.getSampleMillis());
        mCpuEngine.launch();
        LogUtil.d("Cpu module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mCpuEngine == null) {
            LogUtil.d("Cpu module has already uninstalled , skip uninstall.");
            return;
        }
        mCpuEngine.stop();
        mCpuEngine = null;
        LogUtil.d("Cpu module uninstalls successfully");
    }
}
