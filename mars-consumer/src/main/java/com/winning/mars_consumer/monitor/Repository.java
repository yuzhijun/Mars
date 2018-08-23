package com.winning.mars_consumer.monitor;

import com.winning.mars_consumer.utils.CommUtil;
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * data repository
 * Created by yuzhijun on 2018/4/2.
 */
public class Repository {
    private static final int MAX_SIZE = 150;//just store lasted data
    private static Repository mInstance;
    private AccountBean currentAccount;
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
    private final Object mLockForBattery = new Object();
    public BatteryBean getBatteryBean() {
        synchronized (mLockForBattery){
            final BatteryBean batteryBean = null == mBatteryBean ? null : mBatteryBean.clone();
            mBatteryBean = null;
            return batteryBean;
        }
    }
    public void setBatteryBean(BatteryBean batteryBean) {
        synchronized (mLockForBattery){
            mBatteryBean = batteryBean;
        }
    }

    //cpu information
    private LinkedList<CpuBean> mCpuBeans = new LinkedList<>();
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
            if (mCpuBeans.size() < MAX_SIZE){
                mCpuBeans.add(cpuBean);
            }else{
                mCpuBeans.removeFirst();
                mCpuBeans.addLast(cpuBean);
            }
        }
    }

    //crash information
    private LinkedList<CrashBean> mCrashBeans = new LinkedList<>();
    private final Object mLockForCrash = new Object();
    public Collection<CrashBean> getCrashBeans() {
        synchronized (mLockForCrash){
            final Collection<CrashBean> crashBeans = cloneList(mCrashBeans);
            mCrashBeans.clear();
            return crashBeans;
        }
    }
    public void setCrashBean(CrashBean crashBean) {
        synchronized (mLockForCrash){
            if (mCrashBeans.size() < MAX_SIZE){
                mCrashBeans.add(crashBean);
            }else{
                mCrashBeans.removeFirst();
                mCrashBeans.addLast(crashBean);
            }
        }
    }

    //device information
    private DeviceBean mDeviceBean;
    private final Object mLockForDevice = new Object();
    public DeviceBean getDeviceBean() {
        synchronized (mLockForDevice){
            final DeviceBean deviceBean = null == mDeviceBean ? null : mDeviceBean.clone();
            mDeviceBean = null;
            return deviceBean;
        }
    }
    public void setDeviceBean(DeviceBean deviceBean) {
        synchronized (mLockForDevice){
            mDeviceBean = deviceBean;
        }
    }

    //fps information
    private LinkedList<FpsBean> mFpsBeans = new LinkedList<>();
    private final Object mLockForFps = new Object();
    public Collection<FpsBean> getFpsBeans() {
        synchronized (mLockForFps){
            final Collection<FpsBean> fpsBeans = cloneList(mFpsBeans);
            mFpsBeans.clear();
            return fpsBeans;
        }
    }
    public void setFpsBean(FpsBean fpsBean) {
        synchronized (mLockForFps){
            if (mFpsBeans.size() < MAX_SIZE){
                mFpsBeans.add(fpsBean);
            }else{
                mFpsBeans.removeFirst();
                mFpsBeans.addLast(fpsBean);
            }
        }
    }

    //inflate information
    private LinkedList<InflateBean> mInflateBeans = new LinkedList<>();
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
           if (mInflateBeans.size() < MAX_SIZE){
               mInflateBeans.add(inflateBean);
           }else{
               mInflateBeans.removeFirst();
               mInflateBeans.addLast(inflateBean);
           }
       }
    }

    //leak information
    private LinkedList<LeakBean.LeakMemoryBean> mLeakMemoryBeans = new LinkedList<>();
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
            if (mLeakMemoryBeans.size() < MAX_SIZE){
                mLeakMemoryBeans.add(leakMemoryBean);
            }else{
                mLeakMemoryBeans.removeFirst();
                mLeakMemoryBeans.addLast(leakMemoryBean);
            }
        }
    }

    //sm information
    private LinkedList<SmBean> mSmBeans = new LinkedList<>();
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
            if (mSmBeans.size() < MAX_SIZE){
                mSmBeans.add(smBean);
            }else{
                mSmBeans.removeFirst();
                mSmBeans.addLast(smBean);
            }
        }
    }

    //deadlock threads information
    private LinkedList<Thread> mDeadLockThreads = new LinkedList<>();
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
            if (null != deadLockThreads && deadLockThreads.size() > 0){
                if ((mDeadLockThreads.size() + deadLockThreads.size()) < MAX_SIZE){
                    mDeadLockThreads.addAll(deadLockThreads);
                }else{
                    for (int i = 0; i < deadLockThreads.size();i ++){
                        if (null != mDeadLockThreads && mDeadLockThreads.size() > 0){
                            mDeadLockThreads.removeFirst();
                        }
                        mDeadLockThreads.addLast(deadLockThreads.get(i));
                    }
                }
            }
        }
    }

    //traffic information
    private LinkedList<TrafficBean> mTrafficBeans = new LinkedList<>();
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
            if (mTrafficBeans.size() < MAX_SIZE){
                mTrafficBeans.add(trafficBean);
            }else{
                mTrafficBeans.removeFirst();
                mTrafficBeans.addLast(trafficBean);
            }
        }
    }

    //network information
    private LinkedList<NetworkBean> mNetworkBeans = new LinkedList<>();
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
            if (mNetworkBeans.size() < MAX_SIZE){
                mNetworkBeans.add(networkBean);
            }else{
                mNetworkBeans.removeFirst();
                mNetworkBeans.addLast(networkBean);
            }
        }
    }

    //startup information
    private StartupBean mStartupBean;
    private final Object mLockForStartup = new Object();
    public StartupBean getStartupBean() {
        synchronized (mLockForStartup){
            final StartupBean startupBean = null == mStartupBean ? null : mStartupBean.clone();
            mStartupBean = null;
            return startupBean;
        }
    }
    public void setStartupBean(StartupBean startupBean) {
        synchronized (mLockForStartup){
            mStartupBean = startupBean;
        }
    }

    //user behavior information
    private UserBehaviorBean mUserBehavior;
    private final  Object mLockUserBehavior = new Object();
    public UserBehaviorBean getUserBehavior (){
        synchronized (mLockUserBehavior) {
            final UserBehaviorBean userBehaviorBean = null == mUserBehavior ? null : new UserBehaviorBean(mUserBehavior.appKey,mUserBehavior.modelIMEI,cloneList(mUserBehavior.getTree()));
            mUserBehavior = null;
            return userBehaviorBean;
        }
    }

    public void setUserBehavior(UserBehaviorBean userBehavior) {
        synchronized (mLockUserBehavior){
            mUserBehavior = userBehavior;
        }
    }

    //account information
    private LinkedList<AccountBean> mAccountBeans = new LinkedList<>();
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
            currentAccount = accountBean;
            //检测当前账号的可用性
            CommUtil.checkAccountUsable(currentAccount);
            if (mAccountBeans.size() < MAX_SIZE){
                mAccountBeans.add(accountBean);
            }else{
                mAccountBeans.removeFirst();
                mAccountBeans.addLast(accountBean);
            }
        }
    }

    private static <T> Collection<T> cloneList(Collection<T> originList) {
        List<T> dest = new LinkedList<>();
        if (originList == null || originList.isEmpty()) {
            return dest;
        }

        dest.addAll(originList);
        return dest;
    }

    public AccountBean getCurrentAccount() {
        return currentAccount;
    }
}
