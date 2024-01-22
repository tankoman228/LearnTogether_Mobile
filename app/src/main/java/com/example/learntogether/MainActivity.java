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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.learntogether.API.ConnectionManager;
import com.example.learntogether.API.NotificationService;

import java.util.Random;

public class MainActivity extends AppCompatActivity  {

    public static MainActivity THIS;

    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnLogin, btnRegister, btnCancel;

    public static void upd_loading_status(String text) {
        if (THIS == null)
            return;
        THIS.runOnUiThread(() -> {
            THIS.tvStatus.setText(text);
        });
    }
    public static void setIsLoading(boolean isLoading) {
        if (THIS == null)
            return;
        THIS.runOnUiThread(() -> {
            if (isLoading) {
                THIS.tvStatus.setVisibility(View.VISIBLE);
                THIS.progressBar.setVisibility(View.VISIBLE);
                THIS.btnCancel.setVisibility(View.VISIBLE);

                THIS.btnRegister.setVisibility(View.INVISIBLE);
                THIS.btnLogin.setVisibility(View.INVISIBLE);
            }
            else {
                THIS.tvStatus.setVisibility(View.INVISIBLE);
                THIS.progressBar.setVisibility(View.INVISIBLE);
                THIS.btnCancel.setVisibility(View.INVISIBLE);

                THIS.btnRegister.setVisibility(View.VISIBLE);
                THIS.btnLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopService(new Intent(this, NotificationService.class));

        THIS = this;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
            return;
        }

        Thread x = new Thread(() -> {
            if (!ConnectionManager.TryLoadAndConnect(this)) {
                upd_loading_status("Can\'t continue session.\nTrying to sign up");
                setIsLoading(false);
            }
        });
        x.start();

        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnGoEnter);
        btnRegister = findViewById(R.id.btnGoRegister);
        btnCancel = findViewById(R.id.brnCancel);

        btnLogin.setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityLogin.class);
            startActivity(i);
        });
        btnRegister.setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityRegister.class);
            startActivity(i);
        });
        btnCancel.setOnClickListener(l -> {
            setIsLoading(false);
            stopService(new Intent(this, NotificationService.class));
            x.interrupt();
        });



        new Thread(() -> {
            float a = 2f;
            while (true) {
                a += 2.5;
                try {
                    Thread.sleep(10);
                    progressBar.setRotation(a);
                } catch (InterruptedException e) {
                   e.printStackTrace();
                }
                if (a > 360) {
                    a = 0;
                }
            }
        }).start();
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