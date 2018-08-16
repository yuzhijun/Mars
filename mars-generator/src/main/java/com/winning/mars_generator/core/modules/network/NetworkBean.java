package com.winning.mars_generator.core.modules.network;

import com.winning.mars_generator.core.BaseBean;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class NetworkBean extends BaseBean{
    public long startTimeMillis;
    public long endTimeMillis;
    public long respBodySizeByte;
    public String url;
    public  Object[] args;
    public String error;

    public NetworkBean(long startTimeMillis, long endTimeMillis, long respBodySizeByte, String url, Object[] args, String error) {
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.respBodySizeByte = respBodySizeByte;
        this.url = url;
        this.args = args;
        this.error = error;
    }

    @Override
    public String toString() {
        return "RequestBaseInfo{" +
                "startTimeMillis=" + startTimeMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", respBodySizeByte=" + respBodySizeByte +
                ", url='" + url + '\'' +
                '}';
    }
}
