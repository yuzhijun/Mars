package com.winning.mars_consumer.monitor.uploader.network;

import com.winning.mars_consumer.monitor.bean.AppUpdate;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * Created by yuzhijun on 2018/4/2.
 */

public interface ApiService {
    @GET
    Flowable<AppUpdate> getUpadateInfo();
}
