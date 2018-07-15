package com.winning.mars_consumer.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.bean.UsableInfo;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;
import com.winning.mars_generator.utils.DeviceUtil;

import java.util.LinkedHashSet;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

import static com.winning.mars_consumer.utils.Constants.Mapper.ACCOUNT_HANDLER;
import static com.winning.mars_consumer.utils.Constants.Mapper.APP_HANDLER;
import static com.winning.mars_consumer.utils.Constants.Mapper.DEVICE_HANDLER;

public class UsableCheckUtil {
    /**
     * 检查是否被禁用
     * */
    @SuppressLint("CheckResult")
    public static boolean checkUsable(Context context){
        Set<String> devices = SPUtils.getStringSet(DEVICE_HANDLER, null);
        Set<String> appKeys = SPUtils.getStringSet(APP_HANDLER,null);
        Set<String> accounts = SPUtils.getStringSet(ACCOUNT_HANDLER,null);
        if (null == devices && null == appKeys && null == accounts){
            Set<String> innerDevices = new LinkedHashSet<>();
            Set<String> innerAppKeys = new LinkedHashSet<>();
            Set<String> innerAccounts = new LinkedHashSet<>();
            ApiServiceModule.getInstance().getNetworkService()
                    .getUsableInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSubscriber<UsableInfo>() {
                        @Override
                        public void onNext(UsableInfo usableInfo) {
                            if (null != usableInfo){
                                if (null != usableInfo.getModelIMEI()){
                                    innerDevices.add(usableInfo.getModelIMEI());
                                    SPUtils.putStringSet(DEVICE_HANDLER,innerDevices);
                                }

                                if (null != usableInfo.getApp_key()){
                                    innerAppKeys.add(usableInfo.getApp_key());
                                    SPUtils.putStringSet(APP_HANDLER,innerAppKeys);
                                }

                                if (null != usableInfo.getAccounts()){
                                    innerAccounts.addAll(usableInfo.getAccounts());
                                    SPUtils.putStringSet(ACCOUNT_HANDLER,innerAccounts);
                                }

                                confirmUsable(context, innerDevices, innerAppKeys, innerAccounts);
                            }
                        }
                        @Override
                        public void onError(Throwable t) {
                        }
                        @Override
                        public void onComplete() {
                        }
                    });
        }else{
            if (confirmUsable(context, devices, appKeys, accounts)) return false;
        }
        return true;
    }

    private static boolean confirmUsable(Context context, Set<String> devices, Set<String> appKeys, Set<String> accounts) {
        if (null != devices){
            for (String deviceId : devices){
                if (DeviceUtil.getUniquePsuedoDeviceID().equalsIgnoreCase(deviceId)){
                    CommUtil.showDialog(context,"该设备已经被禁用",false);
                    return true;
                }
            }
        }

        if (null != appKeys){
            for (String appkey : appKeys){
                if (MarsEntrance.getInstance().appKey.equalsIgnoreCase(appkey)){
                    CommUtil.showDialog(context,"该应用已经被禁用",false);
                    return true;
                }
            }
        }

        if (null != accounts){
            for (String account : accounts){
                if (null != Repository.getInstance().getCurrentAccount() && Repository.getInstance().getCurrentAccount().getEmpno().equals(account)){
                    CommUtil.showDialog(context,"该账号已经被禁用",false);
                    return true;
                }
            }
        }
        return false;
    }
}
