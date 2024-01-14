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

        findViewById(R.id.btnEnter).setOnClickListener(l -> {
            CurrentAccount.server = etIP.getText().toString();
            CurrentAccount.password = etPwd.getText().toString();
            CurrentAccount.username = etLogin.getText().toString();

            new Thread(() -> {
                Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();

                if (HttpJsonRequest.try_init("80.89.196.150:8000")) {

                    JSONObject json = new JSONObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            json.put("username",  CurrentAccount.username);
                            json.put("password",  CurrentAccount.password);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    HttpJsonRequest.JsonRequestAsync("login/login", json,
                            "POST", new HttpJsonRequest.Callback() {
                                @Override
                                public void onSuccess(JSONObject response) {

                                    try {
                                        if (response.getString("Result").equals("Success")) {
                                            Toast.makeText(ActivityLogin.this, response.toString(), Toast.LENGTH_SHORT).show();

                                            CurrentAccount.AccessToken = response.getString("Token");

                                            ActivityLogin.this.runOnUiThread(() -> {
                                                ActivityLogin.this.startService(
                                                        new Intent(ActivityLogin.this, NotificationService.class));
                                            });

                                            return;
                                        }
                                    }
                                    catch (Exception e) { e.printStackTrace(); }

                                    Toast.makeText(ActivityLogin.this, "ERROR: " + response.toString(), Toast.LENGTH_SHORT).show();
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