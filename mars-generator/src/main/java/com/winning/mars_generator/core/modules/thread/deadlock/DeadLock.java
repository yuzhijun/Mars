package com.winning.mars_generator.core.modules.thread.deadlock;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

import java.util.List;

/**
 * Created by yuzhijun on 2018/3/30.
 */
public class DeadLock extends GeneratorSubject<List<Thread>> implements Install{
    private DeadLockEngine mDeadLockEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getDeadLock());
    }

    private void install(Context context,MarsConfig.DeadLock deadLock){
        if (null != mDeadLockEngine){
            LogUtil.d("Deadlock module has already installed, skip install");
            return;
        }

        mDeadLockEngine = new DeadLockEngine(context,this,deadLock.getIntervalMillis());
        mDeadLockEngine.launch();
        LogUtil.d("Deadlock module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mDeadLockEngine == null) {
            LogUtil.d("Deadlock module has already uninstalled , skip uninstall.");
            return;
        }
        mDeadLockEngine.stop();
        mDeadLockEngine = null;
        LogUtil.d("Deadlock module uninstalls successfully");
    }
}
