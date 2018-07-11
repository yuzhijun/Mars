package com.winning.mars_consumer.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.winning.mars_consumer.MarsConsumer;
import com.winning.mars_consumer.MarsEntrance;
import com.winning.mars_generator.core.modules.account.AccountBean;
import com.winning.mars_generator.core.modules.device.DeviceBean;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
                    showDialog(MarsConsumer.mContext,"该账号已经被禁用");
                }
            }
        }
    }

    public static void showDialog(Context context,String msg){
        if (null != MarsEntrance.getInstance().customForbiddenBehavior){
            MarsEntrance.getInstance().customForbiddenBehavior.onForbiddenBehavior();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)) {//有些手机没有打开系统悬浮窗权限，直接让应用退出
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                System.exit(0);//正常退出App
                return;
            } else {
                showSystemDialog(context, msg);
            }
        } else {
            showSystemDialog(context, msg);
        }
    }

    private static  void showSystemDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
        builder.setTitle("提示");
        builder.setMessage(msg);
        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public static DeviceBean getDeviceInfo(Context context) {
        DeviceBean  mDeviceBean = new DeviceBean();
        TelephonyManager phone = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceBean.setDeviceBrand(Build.BRAND);
        mDeviceBean.setDeviceID(Build.ID);
        mDeviceBean.setDeviceModel(Build.MODEL);
        mDeviceBean.setDeviceSDK(Build.VERSION.SDK_INT + "");
        mDeviceBean.setProductName(Build.PRODUCT);
        mDeviceBean.setModelIP(getIpAddress(context));
        mDeviceBean.setNetworkOperator(phone.getNetworkOperator());
        mDeviceBean.setSimOperatorName(phone.getSimOperatorName());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            mDeviceBean.setModelIMEI(null);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mDeviceBean.setModelIMEI(phone.getImei());
            }else{
                mDeviceBean.setModelIMEI(phone.getDeviceId());
            }
        }

        return mDeviceBean;
    }

    /**
     * get device ip address
     * @return ip address
     * */
    private static String getIpAddress(Context context){
        NetworkInfo info = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//2G/3G/4G network
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//wifi
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//ipv4 address
                return ipAddress;
            }
        }
        return null;
    }

    /**
     * convert int ip to String
     * @param ip (int)
     * @return ip(String)
     * */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
