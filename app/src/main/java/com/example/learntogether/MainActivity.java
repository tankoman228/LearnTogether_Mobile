package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements API_Connectable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        findViewById(R.id.btnGoEnter).setOnClickListener(l -> {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        });
        findViewById(R.id.btnGoRegister).setOnClickListener(l -> {
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        });

        ServiceSocket.Activity = this;

        if (ServiceSocket.THIS == null) {
            Intent intent = new Intent(this, ServiceSocket.class);
            startForegroundService(intent);
        }

        CurrentAccount.TryLoadAccountInfo(this);
        if (ServiceSocket.try_login()) {
            Log.d("API", "AUTO LOGIN SUCCESS");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(String get) {

    }
}