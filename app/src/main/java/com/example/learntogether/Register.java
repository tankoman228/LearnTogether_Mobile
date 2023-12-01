package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText et = findViewById(R.id.etRegisterIP);
        EditText et2 = findViewById(R.id.etRegisterToken);

        findViewById(R.id.btnRegister).setOnClickListener(l -> {
            try {
                String txt = et.getText().toString();

                ServiceSocket.hostname = txt.split(":")[0];
                ServiceSocket.Port = Integer.valueOf( txt.split(":")[1]);
                ServiceSocket.RegisterToken = et2.getText().toString();

                Intent i = new Intent(this, RegisterMenu.class);
                startActivity(i);
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Incorrect data", Toast.LENGTH_SHORT);
            }
        });
    }
}