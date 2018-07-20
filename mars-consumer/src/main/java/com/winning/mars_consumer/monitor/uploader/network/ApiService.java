package com.winning.mars_consumer.monitor.uploader.network;

import com.winning.mars_consumer.monitor.bean.UsableInfo;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yuzhijun on 2018/4/2.
 */

public interface ApiService {
    @GET("/app/getUsableSyncInfo")
    Flowable<UsableInfo> getUsableInfo(@Query("model_IMEI") String model_IMEI, @Query("app_key") String app_key);
}
