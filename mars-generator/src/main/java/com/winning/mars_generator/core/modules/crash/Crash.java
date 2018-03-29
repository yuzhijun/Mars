package com.winning.mars_generator.core.modules.crash;

import android.content.Context;

import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.LogUtil;
import com.winning.mars_generator.utils.Preconditions;

import java.util.List;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by yuzhijun on 2018/3/28.
 */
public class Crash extends GeneratorSubject<List<CrashBean>> implements Install {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private boolean mInstalled;
    private DefaultCrashHelper DEFAULT_CRASH_HELPER ;

    @Override
    public void install(Context context) {
        DEFAULT_CRASH_HELPER = new DefaultCrashHelper(context);
       install(DEFAULT_CRASH_HELPER);
    }

    public void install(ICrashHelper crashHelper){
        if (!mInstalled) {
            Preconditions.checkNotNull(crashHelper);
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new CrashEngine(this, crashHelper, mDefaultHandler));
            mInstalled = true;
            LogUtil.d("Crash module installed");
        } else {
            LogUtil.d("Crash module has already installed , skip");
        }
    }

    @Override
    public void uninstall() {
        if (mInstalled) {
            Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
            mInstalled = false;
            LogUtil.d("Crash module uninstalled");
        } else {
            LogUtil.d("Crash has already uninstalled, skip");
        }
    }

    /**
     * emit lasted event
     * */
    @Override
    protected Subject<List<CrashBean>> createSubject() {
        return BehaviorSubject.create();
    }
}
