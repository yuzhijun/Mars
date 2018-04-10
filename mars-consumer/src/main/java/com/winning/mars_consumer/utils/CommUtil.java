package com.winning.mars_consumer.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

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
}
