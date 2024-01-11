package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        findViewById(R.id.btnGoEnter).setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityLogin.class);
            startActivity(i);
        });
        findViewById(R.id.btnGoRegister).setOnClickListener(l -> {
            Intent i = new Intent(this, ActivityRegister.class);
            startActivity(i);
        });
/*
        ServiceSocket.Activity = this;

        if (ServiceSocket.THIS == null) {
            Intent intent = new Intent(this, ServiceSocket.class);
            startForegroundService(intent);
        }

        CurrentAccount.TryLoadAccountInfo(this);
        if (ServiceSocket.try_login()) {
            Log.d("API", "AUTO LOGIN SUCCESS");
        }*/
    }
}