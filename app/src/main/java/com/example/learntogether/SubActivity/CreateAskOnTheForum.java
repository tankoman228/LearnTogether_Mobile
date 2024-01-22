package com.example.learntogether.SubActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learntogether.API.ConnectionManager;
import com.example.learntogether.API.HttpJsonRequest;
import com.example.learntogether.ActivityCentral;
import com.example.learntogether.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAskOnTheForum extends AppCompatActivity {

    EditText etTitle, etText, etTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ask_on_the_forum);

        etTitle = findViewById(R.id.etHeader);
        etText = findViewById(R.id.etText);
        etTags = findViewById(R.id.etTags);

        findViewById(R.id.btnCancel).setOnClickListener(l -> {
            startActivity(new Intent(this, ActivityCentral.class));
        });
        findViewById(R.id.btnAdd).setOnClickListener(l -> {

            String title = etTitle.getText().toString();
            String text = etText.getText().toString();
            String tags = etTags.getText().toString();

            if (title.equals("") || text.equals("") || tags.equals("")) {
                Toast.makeText(this, "Unfilled fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] tagss = tags.split(",");
            for (String tagg: tagss) {
                if (tagg.length() > 32 || tagg.length() < 1) {
                    Toast.makeText(this, "Tag can\'t mustn\'t be empty or longer than 32 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(this, "Query started. Sending response to server", Toast.LENGTH_LONG).show();

            JSONObject json = new JSONObject();
            try {
                json.put("id_group", 1);
                json.put("title", title);
                json.put("text", text);
                json.put("tags", tags);
                json.put("session_token", ConnectionManager.accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpJsonRequest.JsonRequestAsync("add_forum_ask", json, "POST", new HttpJsonRequest.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    startActivity(new Intent(CreateAskOnTheForum.this, ActivityCentral.class));
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(CreateAskOnTheForum.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}