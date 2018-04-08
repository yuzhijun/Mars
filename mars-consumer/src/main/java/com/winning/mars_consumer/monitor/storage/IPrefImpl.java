package com.winning.mars_consumer.monitor.storage;

/**
 * Created by yuzhijun on 2018/4/8.
 */

public interface IPrefImpl {
    String getPrefString(String key, String defaultValue);

    void setPrefString(String key, String value);

    boolean getPrefBoolean(String key, boolean defaultValue);

    void setPrefBoolean(final String key, final boolean value);

    void setPrefInt(final String key, final int value);

    int getPrefInt(final String key, final int defaultValue);

    void setPrefFloat(final String key, final float value);

    float getPrefFloat(final String key, final float defaultValue);

    void setPrefLong(final String key, final long value);

    long getPrefLong(final String key, final long defaultValue);

    void removePreference(final String key);

    boolean hasKey(String key);
}
