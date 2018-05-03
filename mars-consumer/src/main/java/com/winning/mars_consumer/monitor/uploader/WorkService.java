package com.winning.mars_consumer.monitor.uploader;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.monitor.LocalRepository;
import com.winning.mars_consumer.utils.Constants;
import com.winning.mars_consumer.utils.DefaultPoolExecutor;
import com.winning.mars_consumer.utils.JsonWrapperUtil;
import com.winning.mars_generator.core.modules.account.AccountBean;
import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.core.modules.crash.CrashBean;
import com.winning.mars_generator.core.modules.fps.FpsBean;
import com.winning.mars_generator.core.modules.inflate.InflateBean;
import com.winning.mars_generator.core.modules.leak.LeakBean;
import com.winning.mars_generator.core.modules.network.NetworkBean;
import com.winning.mars_generator.core.modules.sm.SmBean;
import com.winning.mars_generator.core.modules.traffic.TrafficBean;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by yuzhijun on 2018/4/2.
 */
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
            DefaultPoolExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    uploadLocalData(intent);
                }
            });
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
        String battery = LocalRepository.getInstance().getFromLocal(Constants.Mapper.BATTERY);
        if (null != battery && !"".equalsIgnoreCase(battery)){
            JSONObject jsonObject = JsonWrapperUtil.toJsonObject(battery);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.BATTERY, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.BATTERY);
                    }
                });
            }
        }

        //upload cpu data
        String cpu = LocalRepository.getInstance().getFromLocal(Constants.Mapper.CPU);
        if (null != cpu && !"".equalsIgnoreCase(cpu)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(cpu, CpuBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CPU, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.CPU);
                    }
                });
            }
        }

        //upload crash data
        String crash = LocalRepository.getInstance().getFromLocal(Constants.Mapper.CRASH);
        if (null != crash && !"".equalsIgnoreCase(crash)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(crash, CrashBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.CRASH, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.CRASH);
                    }
                });
            }
        }

        //upload device data
        String device = LocalRepository.getInstance().getFromLocal(Constants.Mapper.DEVICE);
        if (null != device && !"".equalsIgnoreCase(device)){
            JSONObject jsonObject = JsonWrapperUtil.toJsonObject(device);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.DEVICE, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.DEVICE);
                    }
                });
            }
        }

        //upload fps data
        String fps = LocalRepository.getInstance().getFromLocal(Constants.Mapper.FPS);
        if (null != fps && !"".equalsIgnoreCase(fps)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(fps, FpsBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.FPS, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.FPS);
                    }
                });
            }
        }

        //upload inflate data
        String inflate = LocalRepository.getInstance().getFromLocal(Constants.Mapper.INFLATE);
        if (null != inflate && !"".equalsIgnoreCase(inflate)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(inflate, InflateBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.INFLATE, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.INFLATE);
                    }
                });
            }
        }

        //upload leak data
        String leak = LocalRepository.getInstance().getFromLocal(Constants.Mapper.LEAK);
        if (null != inflate && !"".equalsIgnoreCase(leak)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(leak, LeakBean.LeakMemoryBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.LEAK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.LEAK);
                    }
                });
            }
        }

        //upload sm data
        String sm = LocalRepository.getInstance().getFromLocal(Constants.Mapper.SM);
        if (null != sm && !"".equalsIgnoreCase(sm)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(sm, SmBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.SM, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.SM);
                    }
                });
            }
        }

        //upload deadLock data
        String deadLock = LocalRepository.getInstance().getFromLocal(Constants.Mapper.DEADLOCK);
        if (null != deadLock && !"".equalsIgnoreCase(deadLock)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(deadLock,Thread.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.DEADLOCK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.DEADLOCK);
                    }
                });
            }
        }

        //upload traffic data
        String traffic = LocalRepository.getInstance().getFromLocal(Constants.Mapper.TRAFFIC);
        if (null != traffic && !"".equalsIgnoreCase(traffic)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(traffic, TrafficBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.TRAFFIC, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.TRAFFIC);
                    }
                });
            }
        }

        //upload network data
        String network = LocalRepository.getInstance().getFromLocal(Constants.Mapper.NETWORK);
        if (null != network && !"".equalsIgnoreCase(network)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(network, NetworkBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.NETWORK, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.NETWORK);
                    }
                });
            }
        }

        //upload startup data
        String startup = LocalRepository.getInstance().getFromLocal(Constants.Mapper.STARTUP);
        if (null != startup && !"".equalsIgnoreCase(startup)){
            JSONObject jsonObject = JsonWrapperUtil.toJsonObject(startup);
            if (null != jsonObject){
                mSocket.emit(Constants.Mapper.STARTUP, jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.STARTUP);
                    }
                });
            }
        }

        //upload account data
        String account = LocalRepository.getInstance().getFromLocal(Constants.Mapper.ACCOUNT);
        if (null != account && !"".equalsIgnoreCase(account)){
            JSONArray jsonArray = JsonWrapperUtil.toJsonArray(account, AccountBean.class);
            if (null != jsonArray){
                mSocket.emit(Constants.Mapper.ACCOUNT, jsonArray, new Ack() {
                    @Override
                    public void call(Object... args) {
                        LocalRepository.getInstance().cleanLocal(Constants.Mapper.ACCOUNT);
                    }
                });
            }
        }

        WakeCPUReceiver.completeWakefulIntent(intent);
    }
}
