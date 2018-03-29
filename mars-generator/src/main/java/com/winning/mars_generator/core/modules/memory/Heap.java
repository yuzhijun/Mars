package com.winning.mars_generator.core.modules.memory;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class Heap extends GeneratorSubject<HeapBean> implements Install {
    private HeapEngine mHeapEngine;

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getHeap());
    }

    private void install(Context context, MarsConfig.Heap heap){
        if (null != mHeapEngine){
            LogUtil.d("Heap module has already installed, skip install");
            return;
        }

        mHeapEngine = new HeapEngine(this,heap.getIntervalMillis());
        mHeapEngine.launch();
        LogUtil.d("Heap module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mHeapEngine == null) {
            LogUtil.d("Heap module has already uninstalled , skip uninstall.");
            return;
        }
        mHeapEngine.stop();
        mHeapEngine = null;
        LogUtil.d("Heap module uninstalls successfully");
    }
}
