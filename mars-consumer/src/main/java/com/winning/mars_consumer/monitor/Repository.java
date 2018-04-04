package com.winning.mars_consumer.monitor;

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

import java.util.ArrayList;
import java.util.Collection;
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
    private List<CpuBean> mCpuBeans = new ArrayList<>();
    private final Object mLockForCpu = new Object();
    public Collection<CpuBean> getCpuBeans() {
        synchronized (mLockForCpu){
            final Collection<CpuBean> cpuBeans = cloneList(mCpuBeans);
            mCpuBeans.clear();
            return cpuBeans;
        }
    }
    public void setCpuBean(CpuBean cpuBean) {
        synchronized (mLockForCpu){
            mCpuBeans.add(cpuBean);
        }
    }

    //crash information
    private List<CrashBean> mCrashBeans = new ArrayList<>();
    private final Object mLockForCrash = new Object();
    public Collection<CrashBean> getCrashBeans() {
        synchronized (mLockForCrash){
            final Collection<CrashBean> crashBeans = cloneList(mCrashBeans);
            mCrashBeans.clear();
            return crashBeans;
        }
    }
    public void setCrashBeans(List<CrashBean> crashBeans) {
        synchronized (mLockForCrash){
            mCrashBeans.addAll(crashBeans);
        }
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
    private List<FpsBean> mFpsBeans = new ArrayList<>();
    private final Object mLockForFps = new Object();
    public Collection<FpsBean> getFpsBeans() {
        synchronized (mLockForFps){
            final Collection<FpsBean> fpsBeans = cloneList(mFpsBeans);
            mFpsBeans.clear();
            return mFpsBeans;
        }
    }
    public void setFpsBean(FpsBean fpsBean) {
        synchronized (mLockForFps){
            mFpsBeans.add(fpsBean);
        }
    }

    //inflate information
    private List<InflateBean> mInflateBeans = new ArrayList<>();
    private final Object mLockForInflate = new Object();
    public Collection<InflateBean> getInflateBeans() {
        synchronized (mLockForInflate){
            final Collection<InflateBean> inflateBeans = cloneList(mInflateBeans);
            mInflateBeans.clear();
            return inflateBeans;
        }
    }
    public void setInflateBean(InflateBean inflateBean) {
       synchronized (mLockForInflate){
           mInflateBeans.add(inflateBean);
       }
    }

    //leak information
    private List<LeakBean.LeakMemoryBean> mLeakMemoryBeans = new ArrayList<>();
    private final Object mLockForLeak = new Object();
    public Collection<LeakBean.LeakMemoryBean> getLeakMemoryBeans() {
        synchronized (mLockForLeak){
            final Collection<LeakBean.LeakMemoryBean> leakMemoryBeans = cloneList(mLeakMemoryBeans);
            mLeakMemoryBeans.clear();
            return leakMemoryBeans;
        }
    }
    public void setLeakMemoryBean(LeakBean.LeakMemoryBean leakMemoryBean) {
        synchronized (mLockForLeak){
            mLeakMemoryBeans.add(leakMemoryBean);
        }
    }

    //sm information
    private List<SmBean> mSmBeans = new ArrayList<>();
    private final Object mLockForSm = new Object();
    public Collection<SmBean> getSmBeans() {
        synchronized (mLockForSm){
            final Collection<SmBean> smBeans = cloneList(mSmBeans);
            mSmBeans.clear();
            return smBeans;
        }
    }
    public void setSmBean(SmBean smBean) {
        synchronized (mLockForSm){
            mSmBeans.add(smBean);
        }
    }

    //deadlock threads information
    private List<Thread> mDeadLockThreads = new ArrayList<>();
    private final Object mLockForDeadLock = new Object();
    public Collection<Thread> getDeadLockThreads() {
        synchronized (mLockForDeadLock){
            final Collection<Thread> deadLockThreads = cloneList(mDeadLockThreads);
            mDeadLockThreads.clear();
            return deadLockThreads;
        }
    }
    public void setDeadLockThreads(List<Thread> deadLockThreads) {
        synchronized (mLockForDeadLock){
            mDeadLockThreads.addAll(deadLockThreads);
        }
    }

    //traffic information
    private List<TrafficBean> mTrafficBeans = new ArrayList<>();
    private final Object mLockForTraffic = new Object();
    public Collection<TrafficBean> getTrafficBeans() {
        synchronized (mLockForTraffic){
            final Collection<TrafficBean> trafficBeans = cloneList(mTrafficBeans);
            mTrafficBeans.clear();
            return trafficBeans;
        }
    }
    public void setTrafficBean(TrafficBean trafficBean) {
        synchronized (mLockForTraffic){
            mTrafficBeans.add(trafficBean);
        }
    }

    //network information
    private List<NetworkBean> mNetworkBeans = new ArrayList<>();
    private final Object mLockForNetwork = new Object();
    public Collection<NetworkBean> getNetworkBeans() {
        synchronized (mLockForNetwork){
            final Collection<NetworkBean> networkBeans = cloneList(mNetworkBeans);
            mNetworkBeans.clear();
            return networkBeans;
        }
    }
    public void setNetworkBean(NetworkBean networkBean) {
        synchronized (mLockForNetwork){
            mNetworkBeans.add(networkBean);
        }
    }

    //startup information
    private StartupBean mStartupBean;
    public StartupBean getStartupBean() {
        return mStartupBean;
    }
    public void setStartupBean(StartupBean startupBean) {
        mStartupBean = startupBean;
    }

    //account information
    private List<AccountBean> mAccountBeans = new ArrayList<>();
    private final Object mLockForAccount = new Object();
    public Collection<AccountBean> getAccountBeans() {
        synchronized (mLockForAccount){
            final Collection<AccountBean> accountBeans = cloneList(mAccountBeans);
            mAccountBeans.clear();
            return accountBeans;
        }
    }
    public void setAccountBean(AccountBean accountBean) {
        synchronized (mLockForAccount){
            mAccountBeans.add(accountBean);
        }
    }

    private static <T> Collection<T> cloneList(Collection<T> originList) {
        List<T> dest = new ArrayList<>();
        if (originList == null || originList.isEmpty()) {
            return dest;
        }
        dest.addAll(originList);
        return dest;
    }
}
