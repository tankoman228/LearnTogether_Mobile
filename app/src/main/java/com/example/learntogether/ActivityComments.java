package com.example.learntogether;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.learntogether.API.ConnectionManager;
import com.example.learntogether.API.HttpJsonRequest;
import com.example.learntogether.Adapters.AdapterComment;
import com.example.learntogether.FromAPI.Comment;
import com.example.learntogether.FromAPI.InfoBase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityComments extends AppCompatActivity {

    public static InfoBase infoBase;

    private ConstraintLayout commentForm;
    private EditText et;
    private SeekBar sb;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        updComments();

        commentForm = findViewById(R.id.commentAddForm);
        et = findViewById(R.id.editTextTextMultiLine);
        sb = findViewById(R.id.seekBar);
        lv = findViewById(R.id.lv);

        findViewById(R.id.btnBack).setOnClickListener(l -> {
            startActivity(new Intent(this, ActivityCentral.class));
        });
        findViewById(R.id.floatingActionButton2).setOnClickListener(l -> {
            commentForm.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.btnCancel).setOnClickListener(l -> {
            commentForm.setVisibility(View.INVISIBLE);
        });

        ((TextView)findViewById(R.id.tvHeader)).setText(infoBase.Title);

        findViewById(R.id.btnSend).setOnClickListener(l -> {

            String text = et.getText().toString();
            if (text.replace(" ", "").equals("")) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json = new JSONObject();
            try {
                json.put("id_object", infoBase.ID_InfoBase);
                json.put("rank", sb.getProgress() + 1);
                json.put("text", text);
                json.put("attachment", null);
                json.put("session_token", ConnectionManager.accessToken);
            }
            catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            HttpJsonRequest.JsonRequestAsync("comments/add_comment", json, "POST", new HttpJsonRequest.Callback() {
                @Override
                public void onSuccess(JSONObject response) {

                    ActivityComments.this.runOnUiThread(() -> {
                        updComments();
                    });
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(ActivityComments.this, "Request error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            commentForm.setVisibility(View.INVISIBLE);
        });
    }

    private void updComments() {

        JSONObject json = new JSONObject();
        try {
            json.put("id_object", infoBase.ID_InfoBase);
            json.put("session_token", ConnectionManager.accessToken);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        HttpJsonRequest.JsonRequestAsync("comments/get_comments", json, "POST", new HttpJsonRequest.Callback() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    JSONArray comments = response.getJSONArray("comments");
                    ArrayList<Comment> comments_list = new ArrayList<>();

                    int i = 0;
                    while (i < comments.length()) {
                        JSONObject o = (JSONObject) comments.get(i);

                        Comment c = new Comment();

                        c.ID_Author = o.getInt("ID_Author");
                        try {
                            c.Avatar = (Bitmap) o.get("Avatar");
                        }
                        catch (Exception r) {
                            r.printStackTrace();
                            c.Avatar = null;
                        }

                        c.Attachment = o.getString("Attachment");
                        if (c.Attachment.equals("null")) {
                            c.Attachment = null;
                        }

                        c.ID_Comment = o.getInt("ID_Comment");
                        c.Author = o.getString("Author");
                        c.DateTime = o.getString("DateTime");
                        c.Text = o.getString("Text");

                        comments_list.add(c);
                        i++;
                    }

                    ActivityComments.this.runOnUiThread(() -> {
                        lv.setAdapter(new AdapterComment(ActivityComments.this, comments_list));
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(ActivityComments.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {}
        });
    }
}