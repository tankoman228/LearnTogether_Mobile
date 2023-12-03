package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity implements API_Connectable {

    EditText etIP, etLogin, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ServiceSocket.Activity = this;

        etIP = findViewById(R.id.etIPServer);
        etLogin = findViewById(R.id.etUsername);
        etPwd = findViewById(R.id.etPassword);

        CurrentAccount.TryLoadAccountInfo(this);

        etIP.setText(CurrentAccount.server);
        etLogin.setText(CurrentAccount.password);

        findViewById(R.id.btnEnter).setOnClickListener(l -> {
            CurrentAccount.server = etIP.getText().toString();
            CurrentAccount.password = etPwd.getText().toString();
            CurrentAccount.username = etLogin.getText().toString();

            CurrentAccount.SaveAccountInfo(this);
            ServiceSocket.try_login();
        });
    }

    @Override
    public void onResponse(String get) {
        Toast.makeText(this, get, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        ServiceSocket.Activity = null;
        super.onDestroy();
    }
}