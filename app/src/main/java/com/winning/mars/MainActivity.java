package com.winning.mars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.winning.mars_consumer.utils.StartupTracer;
import com.winning.mars_consumer.utils.UpdateUtil;

public class MainActivity extends AppCompatActivity {
    private TextView tvLeak;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLeak = findViewById(R.id.tvLeak);

        StartupTracer.get().onHomeCreate(this);
        UpdateUtil.checkUpdate(this,"ddddd");

        tvLeak.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LeakActivity.class);
            startActivity(intent);
        });
    }
}
