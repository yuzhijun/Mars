package com.winning.mars_generator.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Looper;

import java.io.IOException;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public class BaseUtility {

    /**
     * if mars.xml.xml exists or not
     * @param context
     * @return boolean exits or not
     * */
    public static boolean isMarsXMLExists(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] fileNames = assetManager.list("");
            if (fileNames != null && fileNames.length > 0) {
                for (String fileName : fileNames) {
                    if (Const.CONFIGURATION_FILE_NAME.equalsIgnoreCase(fileName)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
        }
        return false;
    }


    /**
     * if in main thread or not
     * @return boolean in or not
     * */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * confirm
     * @param tag for throw exception
     * */
    public static void ensureWorkThread(String tag) {
        if (isMainThread()) {
            throw new IllegalStateException(tag + " operation must execute on main thread!");
        }
    }
}
