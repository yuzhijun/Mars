package com.winning.mars_generator.core;

import android.content.Context;

/**
 * Created by yuzhijun on 2018/3/27.
 */
public interface Install<T> {
    void install(Context context) ;

    void uninstall();
}
