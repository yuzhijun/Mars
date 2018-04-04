package com.winning.mars_generator.core.modules.inflate;

import android.content.Context;

import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * View inflate duration and depth
 * Created by yuzhijun on 2018/3/28.
 */
public class Inflate extends GeneratorSubject<InflateBean> implements Install {
    private InflateEngine mInflateEngine;

    @Override
    public void install(Context context) {
        if (null != mInflateEngine){
            LogUtil.d("Inflate module has already installed, skip install");
            return;
        }

        mInflateEngine = new InflateEngine(this, context);
        mInflateEngine.launch();
        LogUtil.d("Inflate module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mInflateEngine == null) {
            LogUtil.d("Inflate module has already uninstalled , skip uninstall.");
            return;
        }
        mInflateEngine.stop();
        mInflateEngine = null;
        LogUtil.d("Inflate module uninstalls successfully");
    }
}
