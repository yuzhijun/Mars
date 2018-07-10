package com.winning.mars;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.winning.mars.utils.BaseHandler;
import com.winning.mars_consumer.utils.StartupTracer;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler = new StartupHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartupTracer.get().onSplashCreate();

        mHandler.sendEmptyMessageDelayed(0,3000);
    }

    public static final class StartupHandler extends BaseHandler<SplashActivity>{

        StartupHandler(SplashActivity splashActivity) {
            super(splashActivity);
        }

        @Override
        protected void handleMessage(SplashActivity splashActivity, Message msg) {
            Intent intent = new Intent(splashActivity, MainActivity.class);
            splashActivity.startActivity(intent);
            splashActivity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
