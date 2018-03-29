package com.winning.mars_generator.core.modules.memory;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class Ram extends GeneratorSubject<RamBean> implements Install<MarsConfig.Ram>{
    private RamEngine mRamEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getRam());
    }

    private void install(Context context, MarsConfig.Ram ram){
        if (null != mRamEngine){
            LogUtil.d("Ram module has already installed, skip install");
            return;
        }

        mRamEngine = new RamEngine(context,this,ram.getIntervalMillis());
        mRamEngine.launch();
        LogUtil.d("Ram module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mRamEngine == null) {
            LogUtil.d("Ram module has already uninstalled , skip uninstall.");
            return;
        }
        mRamEngine.stop();
        mRamEngine = null;
        LogUtil.d("Ram module uninstalls successfully");
    }
}
