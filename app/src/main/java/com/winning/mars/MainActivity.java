package com.winning.mars;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.winning.mars.model.GirlsData;
import com.winning.mars.network.AppApiService;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppApiService appApiService = ApiServiceModule.getInstance().getNetworkService(AppApiService.class);
        appApiService.getFuliData("3","1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<GirlsData>() {

                    @Override
                    public void onNext(GirlsData girlsData) {
                        GirlsData girlsData1 = girlsData;
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
        });
    }
}
