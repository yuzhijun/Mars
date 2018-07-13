package com.winning.mars_generator.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Looper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public class BaseUtility {
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
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
            throw new IllegalStateException(tag + " operation must execute on work thread!");
        }
    }

    /**
     * confirm
     * @param tag for throw exception
     * */
    public static void ensureMainThread(String tag) {
        if (!isMainThread()) {
            throw new IllegalStateException(tag + " operation must execute on main thread!");
        }
    }

    public static Map<String, List<String>> convertToStackString(Map<Long, List<StackTraceElement>> ts) {
        // filtered stack trace info
        Map<Long, List<StackTraceElement>> filterMap = new LinkedHashMap<>();
        for (Long key : ts.keySet()) {
            List<StackTraceElement> value = ts.get(key);
            if (!filterMap.containsValue(value)) {// filter same stack trace info
                filterMap.put(key, value);
            }
        }
        // convert to String
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<Long, List<StackTraceElement>> entry : filterMap.entrySet()) {
            result.put(TIME_FORMATTER.format(entry.getKey()), getStack(entry.getValue()));
        }
        return result;
    }

    private static List<String> getStack(List<StackTraceElement> stackTraceElements) {
        List<String> stackList = new ArrayList<>();
        for (StackTraceElement traceElement : stackTraceElements) {
            stackList.add(String.valueOf(traceElement));
        }
        return stackList;
    }

    public static String getStack(StackTraceElement... stackTraceElements) {
        StringBuilder sbStackElements = new StringBuilder();
        for (StackTraceElement traceElement : stackTraceElements) {
            sbStackElements.append(String.valueOf(traceElement)+"\n");
        }
        return sbStackElements.toString();
    }

    public static String getString(Context context,int id) {
        return context.getApplicationContext().getString(id);
    }
}
