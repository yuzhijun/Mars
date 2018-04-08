package com.winning.mars_consumer.monitor;

import android.net.Uri;

import com.winning.mars_consumer.monitor.presenter.AccountPresenter;
import com.winning.mars_consumer.monitor.presenter.BatteryPresenter;
import com.winning.mars_consumer.monitor.presenter.CpuPresenter;
import com.winning.mars_consumer.monitor.presenter.CrashPresenter;
import com.winning.mars_consumer.monitor.presenter.DeadLockPresenter;
import com.winning.mars_consumer.monitor.presenter.DevicePresenter;
import com.winning.mars_consumer.monitor.presenter.FpsPresenter;
import com.winning.mars_consumer.monitor.presenter.InflatePresenter;
import com.winning.mars_consumer.monitor.presenter.LeakPresenter;
import com.winning.mars_consumer.monitor.presenter.NetworkPresenter;
import com.winning.mars_consumer.monitor.presenter.SmPresenter;
import com.winning.mars_consumer.monitor.presenter.StartupPresenter;
import com.winning.mars_consumer.monitor.presenter.TrafficPresenter;
import com.winning.mars_consumer.monitor.presenter.base.Presenter;
import com.winning.mars_consumer.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public class PresenterMapper {
    private Map<String,Presenter> mPresenterMap;
    private static PresenterMapper mInstance;
    private PresenterMapper(){
    }

    public static PresenterMapper getInstance(){
        if (null == mInstance){
            synchronized (PresenterMapper.class){
                if (null == mInstance){
                    mInstance = new PresenterMapper();
                }
            }
        }

        return mInstance;
    }

    public void init(){
        mPresenterMap = new HashMap<>();
        AccountPresenter accountPresenter = new AccountPresenter();
        mPresenterMap.put(Constants.Mapper.ACCOUNT,accountPresenter);
        BatteryPresenter batteryPresenter = new BatteryPresenter();
        mPresenterMap.put(Constants.Mapper.BATTERY,batteryPresenter);
        CpuPresenter cpuPresenter = new CpuPresenter();
        mPresenterMap.put(Constants.Mapper.CPU,cpuPresenter);
        CrashPresenter crashPresenter = new CrashPresenter();
        mPresenterMap.put(Constants.Mapper.CRASH,crashPresenter);
        DeadLockPresenter deadLockPresenter = new DeadLockPresenter();
        mPresenterMap.put(Constants.Mapper.DEADLOCK,deadLockPresenter);
        DevicePresenter devicePresenter = new DevicePresenter();
        mPresenterMap.put(Constants.Mapper.DEVICE,devicePresenter);
        FpsPresenter fpsPresenter = new FpsPresenter();
        mPresenterMap.put(Constants.Mapper.FPS,fpsPresenter);
        InflatePresenter inflatePresenter = new InflatePresenter();
        mPresenterMap.put(Constants.Mapper.INFLATE,inflatePresenter);
        LeakPresenter leakPresenter = new LeakPresenter();
        mPresenterMap.put(Constants.Mapper.LEAK,leakPresenter);
        NetworkPresenter networkPresenter = new NetworkPresenter();
        mPresenterMap.put(Constants.Mapper.NETWORK,networkPresenter);
        SmPresenter smPresenter = new SmPresenter();
        mPresenterMap.put(Constants.Mapper.SM,smPresenter);
        StartupPresenter startupPresenter = new StartupPresenter();
        mPresenterMap.put(Constants.Mapper.STARTUP,startupPresenter);
        TrafficPresenter trafficPresenter = new TrafficPresenter();
        mPresenterMap.put(Constants.Mapper.TRAFFIC,trafficPresenter);
    }

    public String process(Uri uri) throws Throwable {
        String presenterName = uri.getPath();
        Presenter presenter = mPresenterMap.get(presenterName);
        if (presenter == null) {
            return null;
        }
        return presenter.process(uri);
    }
}
