package com.winning.mars_consumer.monitor.uploader;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.monitor.Repository;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by yuzhijun on 2018/4/2.
 */
//TODO 兼容5.0以下机子
public class WorkService extends IntentService {
    private boolean isConnected = false;
    private Socket mSocket;

    public WorkService() {
        super("");
    }

    public WorkService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mSocket = MarsConsumer.mSocket;
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();

        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (isConnected){
            DefaultPoolExecutor.getInstance().execute(() -> uploadLocalData(intent));
        }
    }

    @Override
    public void onDestroy() {
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        super.onDestroy();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(!isConnected) {
                isConnected = true;
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
        }
    };

    private synchronized void uploadLocalData(Intent intent){
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

        WakeCPUReceiver.completeWakefulIntent(intent);
    }
}
