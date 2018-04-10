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
            mSocket = IO.socket(Constants.BASE_URL);
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
