package com.winning.mars_consumer.monitor.uploader;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.utils.CommUtil;
import com.winning.mars_consumer.utils.Constants;
import com.winning.mars_consumer.utils.DefaultPoolExecutor;
import com.winning.mars_consumer.utils.JsonWrapperUtil;
import com.winning.mars_consumer.utils.SPUtils;
import com.winning.mars_generator.core.BaseBean;
import com.winning.mars_generator.core.modules.account.AccountBean;
import com.winning.mars_generator.core.modules.battery.BatteryBean;
import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.core.modules.crash.CrashBean;
import com.winning.mars_generator.core.modules.device.DeviceBean;
import com.winning.mars_generator.core.modules.fps.FpsBean;
import com.winning.mars_generator.core.modules.inflate.InflateBean;
import com.winning.mars_generator.core.modules.inflate.UserBehaviorBean;
import com.winning.mars_generator.core.modules.leak.LeakBean;
import com.winning.mars_generator.core.modules.network.NetworkBean;
import com.winning.mars_generator.core.modules.sm.SmBean;
import com.winning.mars_generator.core.modules.startup.StartupBean;
import com.winning.mars_generator.core.modules.traffic.TrafficBean;
import com.winning.mars_generator.utils.DeviceUtil;
import com.winning.mars_generator.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Set;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.winning.mars_consumer.utils.Constants.Mapper.ACCOUNT_HANDLER;
import static com.winning.mars_consumer.utils.Constants.Mapper.APP_HANDLER;
import static com.winning.mars_consumer.utils.Constants.Mapper.DEVICE_HANDLER;

/**
 * Created by yuzhijun on 2018/4/2.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private static final int DEVICE_TYPE = 0;
    private static final int APP_TYPE = 1;
    private static final int ACCOUNT_TYPE = 2;
    private static final int MAX_RETRY = 3;
    private int retry = 0;
    private int mJobId = 0;
    private boolean isConnected = false;
    private Socket mSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSocket = MarsConsumer.mSocket;
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(DEVICE_HANDLER,onDeviceHandler);
        mSocket.on(APP_HANDLER,onAppHandler);
        mSocket.on(ACCOUNT_HANDLER,onAccountHandler);
        mSocket.connect();
        while (scheduleJob() < 0 && retry <= MAX_RETRY){//增加计划任务服务启动失败重试机制
            retry ++;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (null != mSocket){
            mSocket.disconnect();

            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off(DEVICE_HANDLER,onDeviceHandler);
            mSocket.off(APP_HANDLER,onAppHandler);
            mSocket.off(ACCOUNT_HANDLER,onAccountHandler);
        }
        retry = 0;
        cancelAllJobs();
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (isConnected){
            DefaultPoolExecutor.getInstance().execute(() -> uploadLocalData(jobParameters));
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                jobFinished(jobParameters, true);
            } else {
                jobFinished(jobParameters, false);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtil.d(jobParameters.getJobId()+" has already finished");
        return false;
    }

    private Integer scheduleJob(){
        ComponentName mServiceComponent = new ComponentName(this, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, mServiceComponent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(CommUtil.isApkInDebug(MarsConsumer.mContext) ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE); //执行的最小延迟时间
            builder.setOverrideDeadline(CommUtil.isApkInDebug(MarsConsumer.mContext) ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE);  //执行的最长延时时间
            builder.setBackoffCriteria(CommUtil.isApkInDebug(MarsConsumer.mContext) ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        } else {
            // run per 2000 millis
            builder.setPeriodic(CommUtil.isApkInDebug(MarsConsumer.mContext) ? Constants.DEBUG_UPLOAD_RATE : Constants.RELEASE_UPLOAD_RATE);
        }
        //no limited
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
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
            //upload base_info
            BaseBean baseInfo = new BaseBean();
            baseInfo.setAppKey(MarsEntrance.getInstance().appKey);
            baseInfo.setModelIMEI(DeviceUtil.getUniquePsuedoDeviceID());
            baseInfo.setApp_type(CommUtil.isApkInDebug(MarsConsumer.mContext) ? 0 : 1);
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(baseInfo);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.BASE_INFO, jsonObject);
            }
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

    private Emitter.Listener onDeviceHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            backToUI(args, DEVICE_TYPE);
        }
    };

    private Emitter.Listener onAppHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            backToUI(args, APP_TYPE);
        }
    };


    private Emitter.Listener onAccountHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            backToUI(args, ACCOUNT_TYPE);
        }
    };

    /**
     * 上传本地的数据
     * */
    private synchronized void uploadLocalData(JobParameters jobParameters){
        //upload battery data
        BatteryBean battery = Repository.getInstance().getBatteryBean();
        if (null != battery){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(battery);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.BATTERY, jsonObject);
            }
        }

        //upload cpu data
        Collection<CpuBean> cpus = Repository.getInstance().getCpuBeans();
        if (null != cpus && cpus.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(cpus);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CPU, jsonArray);
            }
        }

        //upload crash data
        Collection<CrashBean> crashs = Repository.getInstance().getCrashBeans();
        if (null != crashs && crashs.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(crashs);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CRASH, jsonArray);
            }
        }

        //upload device data
        DeviceBean device = Repository.getInstance().getDeviceBean();
        if (null != device){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(device);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.DEVICE, jsonObject);
            }
        }

        UserBehaviorBean userBehavior = Repository.getInstance().getUserBehavior();
        if (null != userBehavior){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(userBehavior);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.USER_BEHAVIOR, jsonObject);
            }
        }

        //upload fps data
        Collection<FpsBean> fps = Repository.getInstance().getFpsBeans();
        if (null != fps && fps.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(fps);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.FPS, jsonArray);
            }
        }

        //upload inflate data
        Collection<InflateBean> inflates = Repository.getInstance().getInflateBeans();
        if (null != inflates && inflates.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(inflates);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.INFLATE, jsonArray);
            }
        }

        //upload leak data
        Collection<LeakBean.LeakMemoryBean> leaks = Repository.getInstance().getLeakMemoryBeans();
        if (null != leaks && leaks.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(leaks);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.LEAK, jsonArray);
            }
        }

        //upload sm data
        Collection<SmBean> sm = Repository.getInstance().getSmBeans();
        if (null != sm && sm.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(sm);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.SM, jsonArray);
            }
        }

        //upload deadLock data
        Collection<Thread> deadLock = Repository.getInstance().getDeadLockThreads();
        if (null != deadLock && deadLock.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(deadLock);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.DEADLOCK, jsonArray);
            }
        }

        //upload traffic data
        Collection<TrafficBean> traffics = Repository.getInstance().getTrafficBeans();
        if (null != traffics && traffics.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(traffics);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.TRAFFIC, jsonArray);
            }
        }

        //upload network data
        Collection<NetworkBean> network = Repository.getInstance().getNetworkBeans();
        if (null != network && network.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(network);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.NETWORK, jsonArray);
            }
        }

        //upload startup data
        StartupBean startup = Repository.getInstance().getStartupBean();
        if (null != startup){
            JSONObject jsonObject = JsonWrapperUtil.objectToJsonObject(startup);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.STARTUP, jsonObject);
            }
        }

        //upload account data
        Collection<AccountBean> accounts = Repository.getInstance().getAccountBeans();
        if (null != accounts && accounts.size() > 0){
            JSONArray jsonArray = JsonWrapperUtil.listToJsonArray(accounts);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.ACCOUNT, jsonArray);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobFinished(jobParameters, true);
        } else {
            jobFinished(jobParameters, false);
        }
    }

    private void backToUI(Object[] args, int Type) {
        try{
            MarsEntrance.setCheckUsableFlag(false);
            Message msg = new Message();
            msg.what = Type;
            msg.obj = args[0];
            msg.arg1 = (int)args[1];
            new MsgHandler(JobSchedulerService.this).sendMessage(msg);
            if (args[args.length - 1] instanceof Ack){
                Ack ack = (Ack) args[args.length - 1];
                ack.call(args[0]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class  MsgHandler extends Handler{
        WeakReference<JobService> mWeakReference;
        MsgHandler(JobService jobService){
            super(Looper.getMainLooper());
            mWeakReference = new WeakReference<>(jobService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DEVICE_TYPE:
                    try{
                        String modelIMEI = (String) msg.obj;
                        int status = msg.arg1;
                        if (null != modelIMEI && DeviceUtil.getUniquePsuedoDeviceID().equalsIgnoreCase(modelIMEI)) {
                            if(status != 0){//0代表禁用
                                SPUtils.putString(DEVICE_HANDLER, "");
                            }else{
                               SPUtils.putString(DEVICE_HANDLER, modelIMEI);
                            }

                            CommUtil.showDialog(mWeakReference.get(),status == 0 ? "该设备被禁用" : "该设备被启用",status != 0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case APP_TYPE:
                    try{
                        String appKey = (String) msg.obj;
                        int status = msg.arg1;
                        if (null != appKey && MarsEntrance.getInstance().appKey.equalsIgnoreCase(appKey)) {
                            if(status != 0){//0代表禁用
                                SPUtils.putString(APP_HANDLER, "");
                            }else{
                                SPUtils.putString(APP_HANDLER, appKey);
                            }

                            CommUtil.showDialog(mWeakReference.get(),status == 0 ? "该应用被禁用" : "该应用被启用",status != 0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case ACCOUNT_TYPE:
                    try{
                        String account = (String) msg.obj;
                        int status = msg.arg1;
                        if (null != account && null != Repository.getInstance().getCurrentAccount()
                                && Repository.getInstance().getCurrentAccount().getEmpno().equals(account)){
                            Set<String> accounts = SPUtils.getStringSet(ACCOUNT_HANDLER,null);

                            if(status != 0 && accounts.contains(account)){//0代表禁用
                                accounts.remove(account);
                            }else{
                                accounts.add(account);
                            }

                            SPUtils.putStringSet(ACCOUNT_HANDLER,accounts.size() <=0 ? null : accounts);

                            CommUtil.showDialog(mWeakReference.get(),status == 0 ? "该账号被禁用" : "该账号被启用",status != 0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
