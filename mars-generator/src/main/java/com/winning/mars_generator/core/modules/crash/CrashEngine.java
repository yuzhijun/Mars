package com.winning.mars_generator.core.modules.crash;

import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.utils.LogUtil;

import java.util.List;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public class CrashEngine implements Thread.UncaughtExceptionHandler{
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private ICrashHelper mCrashHelper;

    public CrashEngine(Generator<List<CrashBean>> generator, ICrashHelper crashHelper, Thread.UncaughtExceptionHandler defaultHandler) {
        mDefaultHandler = defaultHandler;
        mCrashHelper = crashHelper;
        try {
            if (mCrashHelper != null) {
                generator.generate(mCrashHelper.restoreCrash());
            }
        } catch (Throwable throwable) {
            LogUtil.e(String.valueOf(throwable));
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (mCrashHelper != null) {
                mCrashHelper.storeCrash(new CrashBean(System.currentTimeMillis(), thread, ex));
            }
        } catch (Throwable throwable) {
            LogUtil.e(String.valueOf(throwable));
        }
        mDefaultHandler.uncaughtException(thread, ex);
    }
}
