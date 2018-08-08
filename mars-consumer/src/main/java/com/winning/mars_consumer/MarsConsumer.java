package com.winning.mars_consumer;

import android.content.Context;

import com.winning.mars_consumer.monitor.Monitor;
import com.winning.mars_consumer.monitor.PresenterMapper;
import com.winning.mars_consumer.monitor.server.MarsSocketServer;
import com.winning.mars_consumer.monitor.uploader.JobStarter;
import com.winning.mars_consumer.utils.Constants;
import com.winning.mars_generator.utils.LogUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Entrance
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsConsumer {
    private static boolean mIsWorking = false;
    private static Monitor mMonitor;
    public static Socket mSocket;
    public static Context mContext;

    /**
     * entrance for external call
     * @param context
     * */
    public static synchronized void consume(Context context){
        if (mIsWorking){
            LogUtil.d("Consumer is still working now.");
            return;
        }
        mIsWorking = true;
        if (context == null){
            throw new IllegalStateException("context can not be null.");
        }
        try {
            IO.Options options = new IO.Options();
            //由于医院的网络环境不稳定，有可能设置了这两个参数
            //到时就算到网络环境好的情况下也不会再重连了，因为尝试次数到达指定次数
            options.reconnectionAttempts = Integer.MAX_VALUE;
//            options.reconnectionDelay = 20000;
            options.timeout = 10000;
            mSocket = IO.socket(Constants.SOCKET_URL,options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mContext = context;
        mMonitor = new Monitor();
        mMonitor.startMonitor(context);

        MarsSocketServer.getInstance().startServer();
        JobStarter.getInstance().startJob(context);

        PresenterMapper.getInstance().init();

        LogUtil.d("begin consuming data.");
    }

    /**
     * stop consume data
     * */
    public static synchronized void stop(){
        MarsSocketServer.getInstance().stopServer();
        JobStarter.getInstance().stopJob();
        if (null != mMonitor){
            mMonitor.stopMonitor();
            mMonitor = null;
        }
        mIsWorking = false;

        LogUtil.d("consuming data ends.");
    }
}
