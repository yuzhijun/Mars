package com.winning.mars_generator.core.modules.sm.blockCanary;

import android.content.Context;
import android.support.annotation.WorkerThread;

public interface BlockInterceptor {
    void onStart(Context context);

    void onStop(Context context);

    /**
     * Short Block
     * @param context
     * @param blockTimeMillis
     */
    @WorkerThread
    void onShortBlock(Context context, long blockTimeMillis);

    /**
     * long block
     * @param context
     * @param blockBean
     */
    @WorkerThread
    void onLongBlock(Context context, LongBlockBean blockBean);
}
