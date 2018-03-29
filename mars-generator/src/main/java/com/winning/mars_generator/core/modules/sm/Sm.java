package com.winning.mars_generator.core.modules.sm;

import android.content.Context;

import com.winning.mars_generator.MarsConfig;
import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.core.modules.sm.blockCanary.BlockInterceptor;
import com.winning.mars_generator.core.modules.sm.blockCanary.LongBlockBean;
import com.winning.mars_generator.core.modules.sm.blockCanary.ShortBlockBean;
import com.winning.mars_generator.utils.LogUtil;

/**
 * be similar to BlockCanary
 * Created by yuzhijun on 2018/3/28.
 */
public class Sm extends GeneratorSubject<SmBean> implements Install<MarsConfig.Sm>{
    private static SmEngine mSmEngine;
    private static Sm mInstance;
    private Sm(){
    }
    public static Sm getInstance(){
        if (null == mInstance){
            synchronized (Sm.class){
                if (null == mInstance){
                    mInstance = new Sm();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void install(Context context) {
        install(context,MarsConfig.getSm());
    }

    private void install(Context context, MarsConfig.Sm sm){
        if (null != mSmEngine){
            LogUtil.d("Sm module has already installed, skip install");
            return;
        }

        mSmEngine = new SmEngine(context,this,sm.getLongBlockThreshold(),sm.getShortBlockThreshold(),sm.getDumpInterval());
        mSmEngine.addBlockInterceptor(new BlockInterceptor() {
            @Override
            public void onStart(Context context) {

            }

            @Override
            public void onStop(Context context) {

            }

            @Override
            public void onShortBlock(Context context, long blockTimeMillis) {
                generate(new SmBean(new ShortBlockBean(blockTimeMillis)));
            }

            @Override
            public void onLongBlock(Context context, LongBlockBean blockBean) {
                generate(new SmBean(blockBean));
            }
        });
        mSmEngine.install();
        LogUtil.d("Sm module installed successfully, enjoy");
    }

    @Override
    public void uninstall() {
        if (mSmEngine == null) {
            LogUtil.d("Sm module has already uninstalled , skip uninstall.");
            return;
        }
        mSmEngine.uninstall();
        mSmEngine = null;
        LogUtil.d("Sm module uninstalls successfully");
    }

    public static SmEngine getSmEngine() {
        return getInstance().mSmEngine;
    }
}
