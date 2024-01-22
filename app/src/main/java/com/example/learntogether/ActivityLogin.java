package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.example.learntogether.API.*;

public class ActivityLogin extends AppCompatActivity {

    EditText etIP, etLogin, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etIP = findViewById(R.id.etIPServer);
        etLogin = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPassword);

        etIP.setText(ConnectionManager.server_ip + ':' + ConnectionManager.server_port);
        etLogin.setText(ConnectionManager.username);

        findViewById(R.id.btnEnter).setOnClickListener(l -> {

            String[] server = etIP.getText().toString().split(":");

            ConnectionManager.server_ip = server[0];
            if (server.length > 1) {
                ConnectionManager.server_port = server[1];
            }

            ConnectionManager.password = etPwd.getText().toString();
            ConnectionManager.username = etLogin.getText().toString();

            ConnectionManager.SaveAccountInfo(this);

            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                if (ConnectionManager.TryLogin(this)) {
                    startActivity(new Intent(this, ActivityCentral.class));
                }
            }).start();
        });
    }
}