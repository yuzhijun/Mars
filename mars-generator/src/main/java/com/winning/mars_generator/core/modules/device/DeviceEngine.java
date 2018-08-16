package com.winning.mars_generator.core.modules.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.winning.mars_generator.core.Engine;
import com.winning.mars_generator.core.Generator;
import com.winning.mars_generator.core.modules.memory.MemoryUtil;
import com.winning.mars_generator.utils.DeviceUtil;
import com.winning.mars_generator.utils.LogUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * generate device base info
 * Created by yuzhijun on 2018/3/28.
 */
public class DeviceEngine implements Engine {
    private Generator<DeviceBean> mGenerator;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;

    public DeviceEngine(Generator<DeviceBean> generator, Context context) {
        this.mGenerator = generator;
        this.mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void launch() {
        mCompositeDisposable.add(Observable.fromCallable(new Callable<DeviceBean>() {
            @Override
            public DeviceBean call() throws Exception {
                return getDeviceInfo();
            }
        }).delay(2000, TimeUnit.MILLISECONDS).subscribe(new Consumer<DeviceBean>() {
            @Override
            public void accept(DeviceBean deviceBean) throws Exception {
                mGenerator.generate(deviceBean);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(String.valueOf(throwable));
            }
        }));
    }

    private DeviceBean getDeviceInfo() {
        String OS = System.getProperty("os.name");
        DeviceBean  mDeviceBean = new DeviceBean();
        TelephonyManager phone = (TelephonyManager) mContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceBean.setDeviceBrand(Build.BRAND);
        mDeviceBean.setDeviceID(Build.ID);
        mDeviceBean.setDeviceModel(Build.MODEL);
        mDeviceBean.setDeviceSDK(Build.VERSION.SDK_INT + "");
        mDeviceBean.setProductName(Build.MANUFACTURER);
        mDeviceBean.setModelIP(getIpAddress());
        mDeviceBean.setCzxt(OS != null ? OS.toLowerCase() : "");
        mDeviceBean.setYxnc(MemoryUtil.getRamInfo(mContext).totalMemKb+ "");
        mDeviceBean.setNetworkOperator(phone.getNetworkOperator());
        mDeviceBean.setSimOperatorName(phone.getSimOperatorName());

        try{
            if (DeviceUtil.isSDCardEnable()){
                mDeviceBean.setCckj(DeviceUtil.getTotalInternalMemorySize() + DeviceUtil.getTotalExternalMemorySize() + "");
            }else{
                mDeviceBean.setCckj(DeviceUtil.getTotalInternalMemorySize() + "");
            }
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            mDeviceBean.setScreenWidth(screenWidth+"");
            mDeviceBean.setScreenHeight(screenHeight+"");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000;
                mDeviceBean.setStart_time(bootTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        mDeviceBean.setModelIMEI(DeviceUtil.getUniquePsuedoDeviceID());
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            mDeviceBean.setModelIMEI(null);
//        }else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mDeviceBean.setModelIMEI(phone.getImei());
//            }else{
//                mDeviceBean.setModelIMEI(phone.getDeviceId());
//            }
//        }

        return mDeviceBean;
    }

    @Override
    public void stop() {
        mCompositeDisposable.dispose();
    }

    /**
     * get device ip address
     * @return ip address
     * */
    private String getIpAddress(){
        NetworkInfo info = ((ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
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
                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
    private String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
