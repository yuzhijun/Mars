package com.winning.mars_generator.core.modules.thread;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

import java.util.List;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class ThreadDump extends GeneratorSubject<List<Thread>> implements Install{
    private ThreadEngine mThreadEngine;

    @Override
    public void install(Context context) {
        install(MarsConfig.getThread());
    }

    private void install(MarsConfig.Thread thread){
        if (null != mThreadEngine){
            LogUtil.d("ThreadDump module has already installed, skip install");
            return;
        }

        mThreadEngine = new ThreadEngine(this,thread.getIntervalMillis());
        mThreadEngine.launch();
        LogUtil.d("ThreadDump module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mThreadEngine == null) {
            LogUtil.d("ThreadDump module has already uninstalled , skip uninstall.");
            return;
        }
        mThreadEngine.stop();
        mThreadEngine = null;
        LogUtil.d("ThreadDump module uninstalls successfully");
    }
}
