package com.winning.mars;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LeakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);
    }

    public void leak(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

}
