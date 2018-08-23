package com.winning.mars_consumer.monitor.uploader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.utils.AlarmUtils;
import com.winning.mars_consumer.utils.CommUtil;
import com.winning.mars_consumer.utils.Constants;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class JobStarter {
    private static final String ACTION_WAKE_UP = "com.doze.cpu.wakeup";
    private static JobStarter mInstance;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private int type;

    private JobStarter(){
    }

    public static JobStarter getInstance(){
        if (null == mInstance){
            synchronized (JobStarter.class){
                if (null == mInstance){
                    mInstance = new JobStarter();
                }
            }
        }

        return mInstance;
    }

    public void startJob(Context context){
        if (Build.VERSION.SDK_INT >= LOLLIPOP){
            startJobService(context);
        }else{
            //暂不做兼容，因为google已经不提倡这种方式
//            registerAlarm(context,1);
        }
    }

    private void startJobService(Context context){
        Intent startServiceIntent = new Intent(context, JobSchedulerService.class);
        context.startService(startServiceIntent);
    }

    private void registerAlarm(Context context,int wakeType){
        type = wakeType;
        if (null == mAlarmManager)
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (mPendingIntent != null) mAlarmManager.cancel(mPendingIntent);

        schedule(context);
    }

    private void schedule(Context context){
        Intent intent = new Intent();
        intent.setAction(ACTION_WAKE_UP);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean isdebug = CommUtil.isApkInDebug(MarsConsumer.mContext);
        switch (type) {
            case 0:
                AlarmUtils.setRTCWakeup(mAlarmManager,isdebug ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE, mPendingIntent);
                break;
            case 1:
                AlarmUtils.setElapsedWakeup(mAlarmManager, isdebug ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE, mPendingIntent);
                break;
        }
    }

    public void stopJob(){
        if (mPendingIntent != null) mAlarmManager.cancel(mPendingIntent);
    }
}
