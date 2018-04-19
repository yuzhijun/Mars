package com.winning.mars.network;

import com.winning.mars.model.GirlsData;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppApiService{
    @GET("api/data/福利/{size}/{index}")
    Flowable<GirlsData> getFuliData(@Path("size") String size, @Path("index") String index);
}
