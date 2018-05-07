package com.winning.mars_generator.core.modules.leak;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v4.content.PermissionChecker;

import com.winning.mars_generator.core.GeneratorSubject;
import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.CanaryLog;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.DefaultLeakDirectoryProvider;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.LeakCanary;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.LeakDirectoryProvider;
import com.winning.mars_generator.utils.FileUtil;
import com.winning.mars_generator.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Created by yuzhijun on 2018/3/29.
 */
public class Leak extends GeneratorSubject<LeakBean.LeakMemoryBean> implements Install{
    private LeakDirectoryProvider mLeakDirectoryProvider;
    private static Leak mInstance;
    private Leak(){
    }
    public static Leak getInstance(){
        if (null == mInstance){
            synchronized (Leak.class){
                if (null == mInstance){
                    mInstance = new Leak();
                }
            }
        }
        return mInstance;
    }

    @SuppressLint("CheckResult")
    @Override
    public void install(Context context) {
        final Application application = (Application) context;
        if (LeakCanary.isInAnalyzerProcess(application)) {
            throw new IllegalStateException("can not call install leak");
        }

        permissionNeed(application,Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            if (!aBoolean) {
                throw new IllegalStateException("install leak need permission:" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            uninstall();
            mLeakDirectoryProvider = new DefaultLeakDirectoryProvider(application);
            try {
                clearLeaks();
            } catch (FileUtil.FileException e) {
                LogUtil.e(e.getLocalizedMessage());
            }
            CanaryLog.setLogger(new CanaryLog.Logger() {
                @Override
                public void d(String s, Object... objects) {
                    LogUtil.d(String.format(s, objects));
                }

                @Override
                public void d(Throwable throwable, String s, Object... objects) {
                    LogUtil.e(String.format(s, objects) + "\n" + String.valueOf(throwable));
                }
            });
            LeakCanary.install(application);
            LogUtil.d("LeakCanary installed");
        });
    }

    @Override
    public void uninstall() {
        LeakCanary.uninstall();
        LogUtil.d("LeakCanary uninstalled");
    }

    private void clearLeaks() throws FileUtil.FileException {
        List<File> leakFiles = mLeakDirectoryProvider.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
        for (File f : leakFiles) {
            FileUtil.deleteIfExists(f);
        }
    }

    /**
     * permission exits or not
     * */
    public Observable<Boolean> permissionNeed(final Application application, final String... permissions) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (String p : permissions) {
                    if (PermissionChecker.checkSelfPermission(application, p) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    static LeakDirectoryProvider getLeakDirectoryProvider() {
        return getInstance().mLeakDirectoryProvider;
    }
}
