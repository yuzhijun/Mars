package com.winning.mars_consumer.monitor.storage;

import android.content.Context;

/**
 * Created by yuzhijun on 2018/4/8.
 */
public class MarsPreference {
    Context mContext;

    String fileName;
    private MarsPreference(){

    }

    public MarsPreference(Context context, String name) {
        this.mContext = context;
        this.fileName = name;
    }

    public String getPrefString(final String key, final String defaultValue) {
        return PrefAccessor.getString(mContext, fileName, key, defaultValue);
    }

    public void setPrefString(final String key, final String value) {
        PrefAccessor.setString(mContext, fileName, key, value);
    }

    public boolean getPrefBoolean(final String key, final boolean defaultValue) {
        return PrefAccessor.getBoolean(mContext, fileName, key, defaultValue);
    }

    public void setPrefBoolean(final String key, final boolean value) {
        PrefAccessor.setBoolean(mContext, fileName, key, value);
    }

    public void setPrefInt(final String key, final int value) {
        PrefAccessor.setInt(mContext, fileName, key, value);
    }

    public int getPrefInt(final String key, final int defaultValue) {
        return PrefAccessor.getInt(mContext, fileName, key, defaultValue);
    }

    public void setPrefLong(final String key, final long value) {
        PrefAccessor.setLong(mContext, fileName, key, value);
    }

    public long getPrefLong(final String key, final long defaultValue) {
        return PrefAccessor.getLong(mContext, fileName, key, defaultValue);
    }

    public void removePreference(final String key) {
        PrefAccessor.remove(mContext, fileName, key);
    }
}
