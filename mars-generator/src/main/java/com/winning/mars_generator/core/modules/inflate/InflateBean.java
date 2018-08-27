package com.winning.mars_generator.core.modules.inflate;

import com.winning.mars_generator.core.BaseBean;

/**
 * Created by yuzhijun on 2018/3/28.
 */

public class InflateBean extends BaseBean implements Cloneable{
    private String activity;
    private long inflateDepth;
    private long inflateTime;
    private long startTime;
    private long stayTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStayTime() {
        return stayTime;
    }

    public void setStayTime(long stayTime) {
        this.stayTime = stayTime;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
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

    @Override
    public InflateBean clone() {
        InflateBean sc = null;
        try
        {
            sc = (InflateBean) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return sc;
    }
}
