package com.winning.mars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.winning.mars_consumer.MarsConsumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MarsConsumer.consume(this);
    }
}
