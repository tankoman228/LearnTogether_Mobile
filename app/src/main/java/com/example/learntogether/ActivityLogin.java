package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.example.learntogether.API.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity {

    EditText etIP, etLogin, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etIP = findViewById(R.id.etIPServer);
        etLogin = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPassword);

        etIP.setText(ConnectionData.server_ip + ':' + ConnectionData.server_port);
        etLogin.setText(ConnectionData.username);

        findViewById(R.id.btnEnter).setOnClickListener(l -> {

            String[] server = etIP.getText().toString().split(":");

            ConnectionData.server_ip = server[0];
            if (server.length > 1) {
                ConnectionData.server_port = server[1];
            }

            ConnectionData.password = etPwd.getText().toString();
            ConnectionData.username = etLogin.getText().toString();

            ConnectionData.SaveAccountInfo(this);

            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                ConnectionData.TryLogin(this);
            }).start();
        });
    }
}