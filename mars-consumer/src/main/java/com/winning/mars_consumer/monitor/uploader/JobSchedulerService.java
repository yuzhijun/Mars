package com.winning.mars_consumer.monitor.uploader;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.winning.mars_generator.utils.LogUtil;

/**
 * Created by yuzhijun on 2018/4/2.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private int mJobId = 0;
    private ComponentName mServiceComponent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        cancelAllJobs();
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //TODO Time consuming task, if task finished, should call jobFinished() method
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtil.d(jobParameters.getJobId()+" has already finished");
        return false;
    }

    private Integer scheduleJob(){
        mServiceComponent = new ComponentName(this, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, mServiceComponent);
        // run per 10000 millis
        builder.setPeriodic(10000);
        // wifi only
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        // no need charging
        builder.setRequiresCharging(false);
        // no need device idle
        builder.setRequiresDeviceIdle(false);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        return tm.schedule(builder.build());
    }

    /**
     * Executed when service destroyed.
     */
    public void cancelAllJobs() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }
}
