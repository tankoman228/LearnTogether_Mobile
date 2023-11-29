package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ClientThread clientThread;
    TextView tv;
    EditText et;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        et = findViewById(R.id.editTextText);
        btn = findViewById(R.id.button);

        //ClientThread.context = this;

        clientThread = new ClientThread() {
            @Override
            protected void onGet_message_from_server(String msg) {
                MainActivity.this.runOnUiThread(() -> tv.setText(msg));
            }
        };
        clientThread.run();

        btn.setOnClickListener(l -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                clientThread.send_message(et.getText().toString());
            }
            et.setText("");
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clientThread.close_all();
    }
}