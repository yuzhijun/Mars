package com.winning.mars_consumer.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_consumer.monitor.Repository;
import com.winning.mars_consumer.monitor.bean.UsableInfo;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;
import com.winning.mars_consumer.monitor.uploader.network.HttpResultFunc;
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
        String device = SPUtils.getString(DEVICE_HANDLER, "");
        String appKey = SPUtils.getString(APP_HANDLER,"");
        Set<String> accounts = SPUtils.getStringSet(ACCOUNT_HANDLER,null);
        if ("".equalsIgnoreCase(device) && "".equalsIgnoreCase(appKey) && (null == accounts || accounts.size() <= 0)){
            Set<String> innerAccounts = new LinkedHashSet<>();
            try{
                ApiServiceModule.getInstance().getNetworkService()
                        .getUsableInfo(DeviceUtil.getUniquePsuedoDeviceID(),MarsEntrance.getInstance().appKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new HttpResultFunc<>())
                        .subscribeWith(new DisposableSubscriber<UsableInfo>() {
                            @Override
                            public void onNext(UsableInfo usableInfo) {
                                if (null != usableInfo){
                                    if (null != usableInfo.getAccounts() && usableInfo.getAccounts().size() > 0){
                                        innerAccounts.addAll(usableInfo.getAccounts());
                                    }

                                    SPUtils.putString(DEVICE_HANDLER,usableInfo.getModelIMEI() == null ? "" : usableInfo.getModelIMEI());
                                    SPUtils.putString(APP_HANDLER,usableInfo.getApp_key() == null ? "" : usableInfo.getApp_key());
                                    SPUtils.putStringSet(ACCOUNT_HANDLER,innerAccounts);
                                    confirmUsable(context, usableInfo.getModelIMEI(), usableInfo.getApp_key(), innerAccounts);
                                }
                            }
                            @Override
                            public void onError(Throwable t) {
                            }
                            @Override
                            public void onComplete() {
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            if (confirmUsable(context, device, appKey, accounts)) return false;
        }
        return true;
    }

    private static boolean confirmUsable(Context context, String device, String appKey, Set<String> accounts) {
        if (null != device){
                if (DeviceUtil.getUniquePsuedoDeviceID().equalsIgnoreCase(device)){
                    CommUtil.showDialog(context,"该设备已经被禁用",false);
                    return true;
                }
        }

        if (null != appKey){
                if (MarsEntrance.getInstance().appKey.equalsIgnoreCase(appKey)){
                    CommUtil.showDialog(context,"该应用已经被禁用",false);
                    return true;
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
