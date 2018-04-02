package com.winning.mars_consumer;

import android.content.Context;

import com.winning.mars_consumer.monitor.Monitor;
import com.winning.mars_consumer.monitor.server.MarsSocketServer;
import com.winning.mars_generator.utils.LogUtil;

/**
 * Entrance
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsConsumer {
    private static boolean mIsWorking = false;
    private static Monitor mMonitor;

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

        mMonitor = new Monitor();
        mMonitor.startMonitor(context);

        MarsSocketServer.getInstance().startServer();
    }

    /**
     * stop consume data
     * */
    public static synchronized void stop(){
        MarsSocketServer.getInstance().stopServer();
        if (null != mMonitor){
            mMonitor.stopMonitor();
            mMonitor = null;
        }
        mIsWorking = false;
    }
}
