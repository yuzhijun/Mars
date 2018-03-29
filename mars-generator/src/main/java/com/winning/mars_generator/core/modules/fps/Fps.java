package com.winning.mars_generator.core.modules.fps;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class Fps extends GeneratorSubject<FpsBean> implements Install {
    private FpsEngine mFpsEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getFps());
    }

    private void install(Context context,MarsConfig.Fps fps){
        if (null != mFpsEngine){
            LogUtil.d("Fps module has already installed, skip install");
            return;
        }

        mFpsEngine = new FpsEngine(context,this,fps.getIntervalMillis());
        mFpsEngine.launch();
        LogUtil.d("Fps module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mFpsEngine == null) {
            LogUtil.d("Fps module has already uninstalled , skip uninstall.");
            return;
        }
        mFpsEngine.stop();
        mFpsEngine = null;
        LogUtil.d("Fps module uninstalls successfully");
    }
}
