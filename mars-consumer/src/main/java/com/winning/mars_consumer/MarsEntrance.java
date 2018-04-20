package com.winning.mars_consumer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.winning.mars_consumer.utils.DownloadUtil;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.battery.Battery;
import com.winning.mars_generator.core.modules.cpu.Cpu;
import com.winning.mars_generator.core.modules.crash.Crash;
import com.winning.mars_generator.core.modules.fps.Fps;
import com.winning.mars_generator.core.modules.inflate.Inflate;
import com.winning.mars_generator.core.modules.leak.Leak;
import com.winning.mars_generator.core.modules.sm.Sm;
import com.winning.mars_generator.core.modules.thread.deadlock.DeadLock;
import com.winning.mars_generator.core.modules.traffic.Traffic;
import com.winning.mars_generator.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;

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
    public void init(Context context,String appKey){
        Mars.getInstance(context).install(Cpu.class)
        .install(Battery.class)
        .install(Crash.class)
        .install(Fps.class)
        .install(Inflate.class)
        .install(Leak.class)
        .install(Sm.class)
        .install(DeadLock.class)
        .install(Traffic.class);

        MarsConsumer.consume(context);
    }

    private void download(Context context,String downloadUrl){
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
