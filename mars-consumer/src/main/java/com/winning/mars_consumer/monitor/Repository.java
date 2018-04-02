package com.winning.mars_consumer.monitor;

import com.winning.mars_generator.core.modules.battery.BatteryBean;
import com.winning.mars_generator.core.modules.cpu.CpuBean;
import com.winning.mars_generator.core.modules.crash.CrashBean;
import com.winning.mars_generator.core.modules.device.DeviceBean;
import com.winning.mars_generator.core.modules.fps.FpsBean;
import com.winning.mars_generator.core.modules.inflate.InflateBean;
import com.winning.mars_generator.core.modules.leak.LeakBean;
import com.winning.mars_generator.core.modules.sm.SmBean;
import com.winning.mars_generator.core.modules.traffic.TrafficBean;

import java.util.List;

/**
 * data repository
 * Created by yuzhijun on 2018/4/2.
 */
public class Repository {
    private static Repository mInstance;
    private Repository(){
    }
    public static Repository getInstance(){
        if (null == mInstance){
            synchronized (Repository.class){
                if (null == mInstance){
                    mInstance = new Repository();
                }
            }
        }

        return mInstance;
    }

    //battery information
    private BatteryBean mBatteryBean;
    public BatteryBean getBatteryBean() {
        return mBatteryBean;
    }
    public void setBatteryBean(BatteryBean batteryBean) {
        mBatteryBean = batteryBean;
    }

    //cpu information
    private CpuBean mCpuBean;
    public CpuBean getCpuBean() {
        return mCpuBean;
    }
    public void setCpuBean(CpuBean cpuBean) {
        mCpuBean = cpuBean;
    }

    //crash information
    private List<CrashBean> mCrashBeans;
    public List<CrashBean> getCrashBeans() {
        return mCrashBeans;
    }
    public void setCrashBeans(List<CrashBean> crashBeans) {
        mCrashBeans = crashBeans;
    }

    //device information
    private DeviceBean mDeviceBean;
    public DeviceBean getDeviceBean() {
        return mDeviceBean;
    }
    public void setDeviceBean(DeviceBean deviceBean) {
        mDeviceBean = deviceBean;
    }

    //fps information
    private FpsBean mFpsBean;
    public FpsBean getFpsBean() {
        return mFpsBean;
    }
    public void setFpsBean(FpsBean fpsBean) {
        mFpsBean = fpsBean;
    }

    //inflate information
    private InflateBean mInflateBean;
    public InflateBean getInflateBean() {
        return mInflateBean;
    }
    public void setInflateBean(InflateBean inflateBean) {
        mInflateBean = inflateBean;
    }

    //leak information
    private LeakBean.LeakMemoryBean mLeakMemoryBean;
    public LeakBean.LeakMemoryBean getLeakMemoryBean() {
        return mLeakMemoryBean;
    }
    public void setLeakMemoryBean(LeakBean.LeakMemoryBean leakMemoryBean) {
        mLeakMemoryBean = leakMemoryBean;
    }

    //sm information
    private SmBean mSmBean;
    public SmBean getSmBean() {
        return mSmBean;
    }
    public void setSmBean(SmBean smBean) {
        mSmBean = smBean;
    }

    //deadlock threads information
    private List<Thread> mDeadLockThreads;
    public List<Thread> getDeadLockThreads() {
        return mDeadLockThreads;
    }
    public void setDeadLockThreads(List<Thread> deadLockThreads) {
        mDeadLockThreads = deadLockThreads;
    }

    //traffic information
    private TrafficBean mTrafficBean;
    public TrafficBean getTrafficBean() {
        return mTrafficBean;
    }
    public void setTrafficBean(TrafficBean trafficBean) {
        mTrafficBean = trafficBean;
    }
}
