package com.winning.mars_generator.utils;

import android.util.Log;

import com.winning.mars_generator.MarsConfig;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public class LogUtil {
    private static final String DEFAULT_TAG = "Mars";

    public static final int DEBUG = 2;

    public static final int ERROR = 5;

    public static int level = MarsConfig.getDebug();

    public static void d(String message){
        if (level <= DEBUG) {
            Log.d(DEFAULT_TAG, message);
        }
    }

    public static void d(String tagName, String message) {
        if (level <= DEBUG) {
            Log.d(tagName, message);
        }
    }

    public static void e(String e){
        if (level <= ERROR) {
            Log.e(DEFAULT_TAG, e);
        }
    }

    public static void e(String tagName, String e){
        if (level <= ERROR) {
            Log.e(tagName, e);
        }
    }
}
