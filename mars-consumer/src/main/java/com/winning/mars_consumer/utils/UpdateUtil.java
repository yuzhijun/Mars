package com.winning.mars_consumer.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.winning.mars_consumer.monitor.bean.UpdateInfo;

public class UpdateUtil{
    public static void checkUpdate(Activity activity,String appKey){
        new UpdateAppManager
                .Builder()
                .setActivity(activity)
                .setHttpManager(new UpdateAppHttpUtil())
                .setUpdateUrl(Constants.UPDATE_URL)
                .setAppKey(appKey)
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            UpdateInfo updateInfo = new Gson().fromJson(json,UpdateInfo.class);
                            if (null == updateInfo || null == updateInfo.getData()){
                                return null;
                            }
                            UpdateInfo.UpdateBean updateBean = updateInfo.getData();
                            final String newVersion = updateBean.getVersion_code();
                            PackageManager pm = activity.getPackageManager();
                            PackageInfo info = pm.getPackageInfo(activity.getPackageName(), 0);
                            int oldVersion = info.versionCode;
                            updateAppBean
                                    .setUpdate(Integer.parseInt(newVersion) > oldVersion ? "Yes" : "No")
                                    .setNewVersion(newVersion)
                                    .setApkFileUrl(updateBean.getDownload_url())
                                    .setUpdateLog(updateBean.getApp_intro())
                                    .setTargetSize("")
                                    .setConstraint(updateBean.getUpdate_type() != 0)//0为非强制更新
                                    .setNewMd5(updateAppBean.getNewMd5());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }
                });
    }
}
