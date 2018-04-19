package com.winning.mars_generator.core.modules.inflate;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class InflateBean {
    private String mActivity;
    private long inflateDepth;
    private long inflateTime;
    private long stayTime;

    public long getStayTime() {
        return stayTime;
    }

    public void setStayTime(long stayTime) {
        this.stayTime = stayTime;
    }

    public String getActivity() {
        return mActivity;
    }

    public void setActivity(String activity) {
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
