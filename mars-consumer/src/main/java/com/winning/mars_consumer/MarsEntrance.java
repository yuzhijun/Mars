package com.winning.mars_consumer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.winning.mars_consumer.monitor.bean.AppInfo;
import com.winning.mars_consumer.monitor.bean.AppUpdate;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;
import com.winning.mars_consumer.utils.Constants;
import com.winning.mars_consumer.utils.DigestUtils;
import com.winning.mars_consumer.utils.DownloadUtil;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.battery.Battery;
import com.winning.mars_generator.core.modules.cpu.Cpu;
import com.winning.mars_generator.core.modules.crash.Crash;
import com.winning.mars_generator.core.modules.device.Device;
import com.winning.mars_generator.core.modules.fps.Fps;
import com.winning.mars_generator.core.modules.inflate.Inflate;
import com.winning.mars_generator.core.modules.leak.Leak;
import com.winning.mars_generator.core.modules.leak.leakcanary.android.LeakCanary;
import com.winning.mars_generator.core.modules.sm.Sm;
import com.winning.mars_generator.core.modules.thread.deadlock.DeadLock;
import com.winning.mars_generator.core.modules.traffic.Traffic;
import com.winning.mars_generator.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by yuzhijun on 2018/4/10.
 */

public class MarsEntrance {
    private static MarsEntrance mInstance;

    private MarsEntrance(){

    }

    public static MarsEntrance getInstance(){
        if (null == mInstance){
            synchronized (MarsEntrance.class){
                if (null == mInstance){
                    mInstance = new MarsEntrance();
                }
            }
        }
        return mInstance;
    }

    /**
     * init
     * @Param key appKey
     * */
    public void init(Context context, String appKey, String appSecret){
        Mars.getInstance(context).install(Leak.class);

        if (!LeakCanary.isInAnalyzerProcess(context)){
            Mars.getInstance(context).install(Cpu.class)
                    .install(Battery.class)
                    .install(Crash.class)
                    .install(Fps.class)
                    .install(Inflate.class)
                    .install(Device.class)
                    .install(Sm.class)
                    .install(DeadLock.class)
                    .install(Traffic.class);

            MarsConsumer.consume(context);

            checkAppUpdate(context,appKey,appSecret);
        }
    }

    public void stop(){
        Mars.getInstance(MarsConsumer.mContext).getModule(Battery.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Cpu.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Crash.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Fps.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Leak.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Sm.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(DeadLock.class).uninstall();
        Mars.getInstance(MarsConsumer.mContext).getModule(Traffic.class).uninstall();

        MarsConsumer.stop();
    }

    @SuppressLint("CheckResult")
    private void checkAppUpdate(Context context, String appKey, String appSecret) {
        final AppInfo appInfo = getAppInfo(context,appKey,appSecret);
        ApiServiceModule.getInstance().getNetworkService()
                .getUpadateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<AppUpdate>() {
                    @Override
                    public void onNext(AppUpdate appUpdate) {
                        if (null != appUpdate && null != appInfo){
                            if (appUpdate.getVersionCode() > appInfo.getVersionCode()){
                                download(context, Constants.DOWNLOAD_URL);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public AppInfo getAppInfo(Context context,String appkey,String appSecret){
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(appkey);
        appInfo.setAppSecret(appSecret);
        appInfo.setToken(DigestUtils.md5DigestAsHex(appkey + "_" + appSecret));
        appInfo.setPackageName(context.getPackageName());
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pkgInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appInfo.setVersionName(pkgInfo.versionName);
            appInfo.setVersionCode(pkgInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appInfo;
    }

    public void download(Context context,String downloadUrl){
        DownloadUtil.download(downloadUrl,new DownloadHandler(this,context));
    }

    private void installApk(Context context,String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    static class DownloadHandler extends Handler {
        WeakReference<MarsEntrance> mMarsEntranceReference;
        Context mContext;

        DownloadHandler(MarsEntrance marsEntrance,Context context) {
            mMarsEntranceReference= new WeakReference<>(marsEntrance);
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MarsEntrance marsEntrance = mMarsEntranceReference.get();

            switch (msg.what) {
                case DownloadUtil.DOWNLOAD_ERROR: {
                    LogUtil.d("DL", "error" + msg.obj);
                }
                break;
                case DownloadUtil.DOWNLOAD_CANCEL:{
                    LogUtil.d("DL", "cancel thread id:" + msg.obj);
                }
                break;
                case DownloadUtil.DOWNLOAD_FINISH: {
                    marsEntrance.installApk(mContext,(String) msg.obj);
                }
                break;
                case DownloadUtil.DOWNLOADING: {
                }
                break;
            }
        }
    }
}
