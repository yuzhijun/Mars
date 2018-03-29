package com.winning.mars_generator.core.modules.memory;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class Pss extends GeneratorSubject<PssBean> implements Install {
    private PssEngine mPssEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getPss());
    }

    private void install(Context context,MarsConfig.Pss pss){
        if (null != mPssEngine){
            LogUtil.d("Pss module has already installed, skip install");
            return;
        }

        mPssEngine = new PssEngine(context,this,pss.getIntervalMillis());
        mPssEngine.launch();
        LogUtil.d("Pss module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mPssEngine == null) {
            LogUtil.d("Pss module has already uninstalled , skip uninstall.");
            return;
        }
        mPssEngine.stop();
        mPssEngine = null;
        LogUtil.d("Pss module uninstalls successfully");
    }
}
