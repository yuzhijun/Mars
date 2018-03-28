package com.winning.mars_generator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.winning.mars_generator.core.Install;
import com.winning.mars_generator.utils.BaseUtility;
import com.winning.mars_generator.utils.MarsParser;

import java.util.Map;

/**
 * Entrance
 * Created by yuzhijun on 2018/3/27.
 */
public class Mars {
    @SuppressLint("StaticFieldLeak")
    private static Mars mInstance;
    private Context mContext;
    private Mars(Context context){
        this.mContext = context;
        loadMarsXmlConfiguration(mContext);
    }
    public static Mars getInstance(Context context){
        if (null == mInstance){
            synchronized (Mars.class){
                if (null == mInstance){
                    mInstance = new Mars(context);
                }
            }
        }
        return mInstance;
    }

    public final <T> Mars install(Class<? extends Install<T>> clz) {
        getModule(clz).install(mContext);
        return this;
    }

    private Map<Class, Object> mCachedModules = new ArrayMap<>();
    @SuppressWarnings("unchecked")
    public <T> T getModule(Class<T> clz) {
        Object module = mCachedModules.get(clz);
        if (module != null) {
            if (!clz.isInstance(module)) {
                throw new IllegalStateException(clz.getName() + " must be instance of " + String.valueOf(module));
            }
            return (T) module;
        }
        try {
            T createdModule;
            createdModule = clz.newInstance();
            mCachedModules.put(clz, createdModule);
            return createdModule;
        } catch (Throwable e) {
            throw new IllegalStateException("Can not create instance of " + clz.getName() + ", " + String.valueOf(e));
        }
    }

    /**
     * load mars.xml.xml
     * @param context
     * */
    private void loadMarsXmlConfiguration(Context context){
        if (BaseUtility.isMarsXMLExists(context)){
            MarsParser.parseMarsConfiguration(context);
        }
    }
}
