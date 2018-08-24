package com.winning.mars_consumer.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_generator.core.modules.account.AccountBean;

import java.util.Set;

import static com.winning.mars_consumer.utils.Constants.Mapper.ACCOUNT_HANDLER;

/**
 * Created by yuzhijun on 2018/4/10.
 */

public class CommUtil {

    /**
     * is apk in debug status
     * @param context
     * @return true or false
     * */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void checkAccountUsable(AccountBean accountBean){
        Set<String> accounts = SPUtils.getStringSet(ACCOUNT_HANDLER,null);
        if (null != accounts){
            for (String account : accounts){
                if (null != accountBean && accountBean.getName().equals(account)){
                    showDialog(MarsConsumer.mContext,"该账号已经被禁用",false);
                }
            }
        }
    }

    /**
     * @param status true 为启用,false为禁用
     * */
    public static void showDialog(Context context,String msg,boolean status){
        if (status){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                showSystemDialog(context, msg, status);
            }
            return;
        }
        if (null != MarsEntrance.getInstance().customForbiddenBehavior){
            MarsEntrance.getInstance().customForbiddenBehavior.onForbiddenBehavior();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)) {//有些手机没有打开系统悬浮窗权限，直接让应用退出
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.exit(0);//正常退出App
                return;
            } else {
                showSystemDialog(context, msg, status);
            }
        } else {
            showSystemDialog(context, msg, status);
        }
    }

    private static  void showSystemDialog(Context context, String msg,boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
        builder.setTitle("提示");
        builder.setMessage(msg);
        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(status);
        dialog.setCancelable(status);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
