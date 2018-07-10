package com.winning.mars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.winning.mars.model.GirlsData;
import com.winning.mars.network.AppApiService;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;
import com.winning.mars_consumer.utils.StartupTracer;
import com.winning.mars_consumer.utils.UpdateUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {
    private TextView tvLeak;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLeak = findViewById(R.id.tvLeak);

        StartupTracer.get().onHomeCreate(this);

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

        UpdateUtil.checkUpdate(this);
        tvLeak.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LeakActivity.class);
            startActivity(intent);
        });
    }
}
