package com.winning.mars_generator.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by yuzhijun on 2017/8/10.
 * get device identified id
 */
public class DeviceUtil {
    private static final String TAG = "DeviceUtils";
    public static final int REQUESTCODE_PERMISSION_READ_PHONE_STATE = 0x0001;
    private static final int REQUESTCODE_PERMISSION_BLUETOOTH = 0x0002;
    /**
     * Return pseudo unique ID
     * @return ID
     */
    public static String getUniquePsuedoDeviceID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" +
                (Build.BOARD.length() % 10) +
                (Build.BRAND.length() % 10) +
                (Build.CPU_ABI.length() % 10) +
                (Build.DEVICE.length() % 10) +
                (Build.MANUFACTURER.length() % 10) +
                (Build.MODEL.length() % 10) +
                (Build.PRODUCT.length() % 10);
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial"; // some value
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    public static String getMyUUID(AppCompatActivity context) {
        String uniqueId = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUESTCODE_PERMISSION_READ_PHONE_STATE);
        } else {

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice, tmSerial, tmPhone, androidId;

            tmDevice = "" + tm.getDeviceId();

            tmSerial = "" + tm.getSimSerialNumber();

            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

            uniqueId = deviceUuid.toString();

//            Log.i("debug", "uuid=" + uniqueId);

            return uniqueId;

        }

        return uniqueId;
    }


    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    public static Boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断蓝牙是否有效来判断是否为模拟器
     *
     * @return true 为模拟器
     */
    public static boolean notHasBlueTooth(AppCompatActivity context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH}, REQUESTCODE_PERMISSION_BLUETOOTH);
        } else {

            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
            if (ba == null) {
                return true;
            } else {
                // 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
                String name = ba.getName();
                if (TextUtils.isEmpty(name)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 根据部分特征参数设备信息来判断是否为模拟器
     *
     * @return true 为模拟器
     */
    public static boolean isFeatures() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    /**
     * 判断cpu是否为电脑来判断 模拟器
     * @return true 为模拟器
     */
    public static boolean checkIsNotRealPhoneAccordingCpu() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机内部空间总大小
     * @return 大小，KB为单位
     */
     public static long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSize();
        //区块总数
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize / 1024;
    }

    /**
     * 获取手机内部可用空间大小
     * @return 大小，KB为单位
     */
     public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / 1024;
    }

    /**
     * 判断SD卡是否可用
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取手机外部总空间大小
     * @return 总大小，kb为单位
     */
     public static long getTotalExternalMemorySize() {
        if (isSDCardEnable()) {
            //获取SDCard根目录
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize / 1024;
        } else {
            return -1;
        }
    }

    /**
     * 获取SD卡剩余空间
     * @return SD卡剩余空间 kb
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getFreeSpace() {
        if (!isSDCardEnable()) return "sdcard unable!";
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        long size = availableBlocks * blockSize / 1024L;
        return String.valueOf(size);
    }
}
