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
        mPresenterMap.put("Account",accountPresenter);
        BatteryPresenter batteryPresenter = new BatteryPresenter();
        mPresenterMap.put("Battery",batteryPresenter);
        CpuPresenter cpuPresenter = new CpuPresenter();
        mPresenterMap.put("Cpu",cpuPresenter);
        CrashPresenter crashPresenter = new CrashPresenter();
        mPresenterMap.put("Crash",crashPresenter);
        DeadLockPresenter deadLockPresenter = new DeadLockPresenter();
        mPresenterMap.put("Deadlock",deadLockPresenter);
        DevicePresenter devicePresenter = new DevicePresenter();
        mPresenterMap.put("Device",devicePresenter);
        FpsPresenter fpsPresenter = new FpsPresenter();
        mPresenterMap.put("Fps",fpsPresenter);
        InflatePresenter inflatePresenter = new InflatePresenter();
        mPresenterMap.put("Inflate",inflatePresenter);
        LeakPresenter leakPresenter = new LeakPresenter();
        mPresenterMap.put("Leak",leakPresenter);
        NetworkPresenter networkPresenter = new NetworkPresenter();
        mPresenterMap.put("Network",networkPresenter);
        SmPresenter smPresenter = new SmPresenter();
        mPresenterMap.put("Sm",smPresenter);
        StartupPresenter startupPresenter = new StartupPresenter();
        mPresenterMap.put("Startup",startupPresenter);
        TrafficPresenter trafficPresenter = new TrafficPresenter();
        mPresenterMap.put("Traffic",trafficPresenter);
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
