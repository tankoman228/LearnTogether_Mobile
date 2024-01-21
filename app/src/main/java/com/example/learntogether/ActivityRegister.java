package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learntogether.API.ConnectionManager;
import com.example.learntogether.API.HttpJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityRegister extends AppCompatActivity {

    EditText etUsername, etTitle, etContact, etPassword, etPassword2, etToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etContact = findViewById(R.id.etContact);
        etUsername = findViewById(R.id.etUsername);
        etTitle = findViewById(R.id.etTitle);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPasswordConfirm);
        etToken = findViewById(R.id.etToken);

        findViewById(R.id.btnRegisterProfile).setOnClickListener(l -> {

            String pwd = etPassword.getText().toString();
            if (!pwd.equals(etPassword2.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", etToken.getText().toString());
                jsonObject.put("username", etUsername.getText().toString());
                jsonObject.put("contact", etContact.getText().toString());
                jsonObject.put("password", pwd);
                jsonObject.put("title", etTitle.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            HttpJsonRequest.JsonRequestAsync("login/register", jsonObject, "POST", new HttpJsonRequest.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getString("Result").equals("Success")) {
                            ConnectionManager.accessToken = response.getString("Token");

                            ActivityRegister.this.runOnUiThread(() -> {
                                ActivityRegister.this.startActivity(new Intent(ActivityRegister.this, ActivityCentral.class));
                            });

                        } else if (response.has("Error")) {

                            Toast.makeText(ActivityRegister.this, response.getString("Error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ActivityRegister.this, "Error: incorrect data from server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityRegister.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}