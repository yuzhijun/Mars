package com.winning.mars_generator.core.modules.inflate;

import android.app.Activity;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class InflateBean {
    private Activity mActivity;
    private long inflateDepth;
    private long inflateTime;

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public long getInflateDepth() {
        return inflateDepth;
    }

    public void setInflateDepth(long inflateDepth) {
        this.inflateDepth = inflateDepth;
    }

    public long getInflateTime() {
        return inflateTime;
    }

    public void setInflateTime(long inflateTime) {
        this.inflateTime = inflateTime;
    }
}
