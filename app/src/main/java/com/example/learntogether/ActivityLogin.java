package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

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

        findViewById(R.id.btnEnter).setOnClickListener(l -> {
            CurrentAccount.server = etIP.getText().toString();
            CurrentAccount.password = etPwd.getText().toString();
            CurrentAccount.username = etLogin.getText().toString();

            new Thread(() -> {
                Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();

                if (HttpJsonRequest.try_init("192.168.3.73:8000")) {
                    HttpJsonRequest.JsonRequestAsync("login/login",
                            "{ \"username\" : \"" + CurrentAccount.username + "\", \"password\" : \"" + CurrentAccount.password + "\"}",
                            "POST", new HttpJsonRequest.Callback() {
                                @Override
                                public void onSuccess(String response) {
                                    if (response.contains("Success")) {
                                        Toast.makeText(ActivityLogin.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ActivityLogin.this, response, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(ActivityLogin.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    Toast.makeText(this, "No connection to server", Toast.LENGTH_SHORT).show();
                }

            }).run();
        });
    }
}