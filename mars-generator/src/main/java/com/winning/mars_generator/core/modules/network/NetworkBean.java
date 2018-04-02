package com.winning.mars_generator.core.modules.network;

/**
 * Created by yuzhijun on 2018/4/2.
 */
public class NetworkBean {
    public long startTimeMillis;
    public long endTimeMillis;
    public long respBodySizeByte;
    public String url;

    public NetworkBean(long startTimeMillis, long endTimeMillis, long respBodySizeByte, String url) {
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.respBodySizeByte = respBodySizeByte;
        this.url = url;
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
