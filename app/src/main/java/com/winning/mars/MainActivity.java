package com.winning.mars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.winning.mars.model.GirlsData;
import com.winning.mars.network.AppApiService;
import com.winning.mars_consumer.monitor.uploader.network.ApiServiceModule;
import com.winning.mars_consumer.utils.StartupTracer;
import com.winning.mars_consumer.utils.UpdateUtil;
import com.winning.mars_generator.Mars;
import com.winning.mars_generator.core.modules.account.Account;
import com.winning.mars_generator.core.modules.account.AccountBean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {
    private TextView tvLeak;
    private TextView tvCrash;
    private TextView tvUpdate;
    private TextView tvNetWork;
    private TextView tvLogin;
    private TextView tvInflate;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLeak = findViewById(R.id.tvLeak);
        tvCrash = findViewById(R.id.tvCrash);
        tvUpdate = findViewById(R.id.tvUpdate);
        tvNetWork = findViewById(R.id.tvNetWork);
        tvLogin = findViewById(R.id.tvLogin);
        tvInflate = findViewById(R.id.tvInflate);

        StartupTracer.get().onHomeCreate(this);

        tvLeak.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LeakActivity.class);
            startActivity(intent);
        });

        tvCrash.setOnClickListener(v -> {
            int A = 100;
            int B = 0;
            double i = A / B;
            Log.e(this.getClass().getSimpleName(),"generate crash info");
        });

        tvUpdate.setOnClickListener(v -> {
            UpdateUtil.checkUpdate(this,"ddddd");
        });

        tvNetWork.setOnClickListener(v -> {
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

            Toast.makeText(this,"网络模拟成功，请去后台查看上传数据",Toast.LENGTH_SHORT).show();
        });

        tvLogin.setOnClickListener(v -> {
            String name = "yuzhijun";
            String pwd = "123";
            Mars.getInstance(this).getModule(Account.class).generate(new AccountBean(name,"123"));
            Toast.makeText(this,"name="+name+";password="+pwd+"已经登录，如果禁用名单上面有这个禁用账户，将会禁用",Toast.LENGTH_SHORT).show();
        });

        tvInflate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InflateActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
