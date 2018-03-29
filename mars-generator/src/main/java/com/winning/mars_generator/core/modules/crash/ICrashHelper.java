package com.winning.mars_generator.core.modules.crash;

import java.util.List;

/**
 * Created by yuzhijun on 2018/3/29.
 */

public interface ICrashHelper {
    void storeCrash(CrashBean crashInfo) throws Throwable;

    List<CrashBean> restoreCrash() throws Throwable;
}
