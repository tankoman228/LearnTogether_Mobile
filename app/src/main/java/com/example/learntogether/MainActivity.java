package com.example.learntogether;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.learntogether.API.ConnectionManager;

public class MainActivity extends AppCompatActivity  {

    public static MainActivity THIS;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        THIS = this;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
            return;
        }

        findViewById(R.id.btnGoEnter).setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityLogin.class);
            startActivity(i);
        });
        findViewById(R.id.btnGoRegister).setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityRegister.class);
            startActivity(i);
        });

        new Thread(() -> {
            if (!ConnectionManager.TryLoadAndConnect(this)) {
                Log.d("API", "Auto Login!");
                ConnectionManager.TryLogin(this);
            }
        }).start();
/*
        ServiceSocket.Activity = this;

        if (ServiceSocket.THIS == null) {
            Intent intent = new Intent(this, ServiceSocket.class);
            startForegroundService(intent);
        }

        ConnectionManager.TryLoadAccountInfo(this);
        if (ServiceSocket.try_login()) {
            Log.d("API", "AUTO LOGIN SUCCESS");
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        THIS = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        THIS = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        THIS = this;
    }
}