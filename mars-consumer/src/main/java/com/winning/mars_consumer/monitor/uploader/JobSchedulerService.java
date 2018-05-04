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

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.utils.CommUtil;
import com.winning.mars_consumer.utils.Constants;
import com.winning.mars_consumer.utils.DefaultPoolExecutor;
import com.winning.mars_consumer.utils.JsonWrapperUtil;
import com.winning.mars_generator.core.modules.account.AccountBean;
import com.winning.mars_generator.core.modules.battery.BatteryBean;
import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.core.modules.crash.CrashBean;
import com.winning.mars_generator.core.modules.device.DeviceBean;
import com.winning.mars_generator.core.modules.fps.FpsBean;
import com.winning.mars_generator.core.modules.inflate.InflateBean;
import com.winning.mars_generator.core.modules.leak.LeakBean;
import com.winning.mars_generator.core.modules.network.NetworkBean;
import com.winning.mars_generator.core.modules.sm.SmBean;
import com.winning.mars_generator.core.modules.startup.StartupBean;
import com.winning.mars_generator.core.modules.traffic.TrafficBean;
import com.winning.mars_generator.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by yuzhijun on 2018/4/2.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private int mJobId = 0;
    private boolean isConnected = false;
    private ComponentName mServiceComponent;
    private Socket mSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSocket = MarsConsumer.mSocket;
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();

        scheduleJob();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        cancelAllJobs();
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (isConnected){
            DefaultPoolExecutor.getInstance().execute(() -> uploadLocalData(jobParameters));
        }
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

        // run per 2000 millis
        builder.setPeriodic(CommUtil.isApkInDebug(MarsConsumer.mContext) ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE);
        // wifi only
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        // no need charging
        builder.setRequiresCharging(false);
        // no need device idle
        builder.setRequiresDeviceIdle(false);
        builder.setPersisted(true);

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

    private Emitter.Listener onConnect = args -> {
        if(!isConnected) {
            isConnected = true;
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
        }
    };

    private synchronized void uploadLocalData(JobParameters jobParameters){
        //upload battery data
        BatteryBean battery = Repository.getInstance().getBatteryBean();
        if (null != battery){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(battery);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.BATTERY, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload cpu data
        Collection<CpuBean> cpus = Repository.getInstance().getCpuBeans();
        if (null != cpus && cpus.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(cpus);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CPU, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload crash data
        Collection<CrashBean> crashs = Repository.getInstance().getCrashBeans();
        if (null != crashs && crashs.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(crashs);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CRASH, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload device data
        DeviceBean device = Repository.getInstance().getDeviceBean();
        if (null != device){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(device);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.DEVICE, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload fps data
        Collection<FpsBean> fps = Repository.getInstance().getFpsBeans();
        if (null != fps && fps.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(fps);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.FPS, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload inflate data
        Collection<InflateBean> inflates = Repository.getInstance().getInflateBeans();
        if (null != inflates && inflates.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(inflates);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.INFLATE, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload leak data
        Collection<LeakBean.LeakMemoryBean> leaks = Repository.getInstance().getLeakMemoryBeans();
        if (null != leaks && leaks.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(leaks);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.LEAK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload sm data
        Collection<SmBean> sm = Repository.getInstance().getSmBeans();
        if (null != sm && sm.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(sm);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.SM, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload deadLock data
        Collection<Thread> deadLock = Repository.getInstance().getDeadLockThreads();
        if (null != deadLock && deadLock.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(deadLock);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.DEADLOCK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload traffic data
        Collection<TrafficBean> traffics = Repository.getInstance().getTrafficBeans();
        if (null != traffics && traffics.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(traffics);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.TRAFFIC, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload network data
        Collection<NetworkBean> network = Repository.getInstance().getNetworkBeans();
        if (null != network && network.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(network);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.NETWORK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload startup data
        StartupBean startup = Repository.getInstance().getStartupBean();
        if (null != startup){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(startup);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.STARTUP, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        //upload account data
        Collection<AccountBean> accounts = Repository.getInstance().getAccountBeans();
        if (null != accounts && accounts.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(accounts);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.ACCOUNT, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                    }
                });
            }
        }

        jobFinished(jobParameters,false);
    }
}
