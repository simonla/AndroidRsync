package com.complexzeng.androidrsync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startServiceBt = findViewById(R.id.bt_start_service);
        startServiceBt.setOnClickListener(v -> startRsyncForegroundService());
    }

    private void startRsyncForegroundService() {
        Context ctx = getApplicationContext();
        Intent intent = new Intent(ctx, RsyncService.class);
        ctx.startForegroundService(intent);
    }

}