package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.learntogether.API.ForumLoader;
import com.example.learntogether.Adapters.ForumAdapter;
import com.example.learntogether.SubActivity.CreateAskOnTheForum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityCentral extends AppCompatActivity {

    ImageButton btnNews, btnFiles, btnMeetings, btnDiscuss, btnPeople;
    ListView lv;
    FloatingActionButton btnAdd;

    enum Mode {
        News, Files, Meetings, Discuss, People
    }
    Mode currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        btnNews = findViewById(R.id.btnNews);
        btnFiles = findViewById(R.id.btnFiles);
        btnMeetings = findViewById(R.id.btnMeetings);
        btnDiscuss = findViewById(R.id.btnDiscuss);
        btnPeople = findViewById(R.id.btnPeople);

        btnNews.setOnClickListener(l -> switchMode(Mode.News));
        btnFiles.setOnClickListener(l -> switchMode(Mode.Files));
        btnMeetings.setOnClickListener(l -> switchMode(Mode.Meetings));
        btnDiscuss.setOnClickListener(l -> switchMode(Mode.Discuss));
        btnPeople.setOnClickListener(l -> switchMode(Mode.People));

        btnAdd = findViewById(R.id.floatingActionButton);
        btnAdd.setOnClickListener(l -> {
            switch (currentMode) {
                case Discuss:
                    startActivity(new Intent(this, CreateAskOnTheForum.class));
                    break;
            }
        });


        lv = findViewById(R.id.lv);
    }

    private void switchMode(Mode i) {
        currentMode = i;
        refreshButtonsColor();

        switch (i) {
            case Discuss:
                load_forum();
                break;
        }
    }

    private void load_forum() {
        new Thread(() -> {
            Log.d("API", "Loading Forum");
            ForumLoader.Reload(99999, 1);

            ActivityCentral.this.runOnUiThread(() -> {
                lv.setAdapter(new ForumAdapter(ActivityCentral.this, ForumLoader.Asks));
            });
        }).start();
    }

    private void refreshButtonsColor() {
        switch (currentMode) {
            case News:
                btnNews.setBackgroundColor(getColor(R.color.black));
                btnFiles.setBackgroundColor(getColor(R.color.black2));
                btnMeetings.setBackgroundColor(getColor(R.color.black2));
                btnDiscuss.setBackgroundColor(getColor(R.color.black2));
                btnPeople.setBackgroundColor(getColor(R.color.black2));
                break;
            case Files:
                btnNews.setBackgroundColor(getColor(R.color.black2));
                btnFiles.setBackgroundColor(getColor(R.color.black));
                btnMeetings.setBackgroundColor(getColor(R.color.black2));
                btnDiscuss.setBackgroundColor(getColor(R.color.black2));
                btnPeople.setBackgroundColor(getColor(R.color.black2));
                break;
            case Meetings:
                btnNews.setBackgroundColor(getColor(R.color.black2));
                btnFiles.setBackgroundColor(getColor(R.color.black2));
                btnMeetings.setBackgroundColor(getColor(R.color.black));
                btnDiscuss.setBackgroundColor(getColor(R.color.black2));
                btnPeople.setBackgroundColor(getColor(R.color.black2));
                break;
            case Discuss:
                btnNews.setBackgroundColor(getColor(R.color.black2));
                btnFiles.setBackgroundColor(getColor(R.color.black2));
                btnMeetings.setBackgroundColor(getColor(R.color.black2));
                btnDiscuss.setBackgroundColor(getColor(R.color.black));
                btnPeople.setBackgroundColor(getColor(R.color.black2));
                break;
            case People:
                btnNews.setBackgroundColor(getColor(R.color.black2));
                btnFiles.setBackgroundColor(getColor(R.color.black2));
                btnMeetings.setBackgroundColor(getColor(R.color.black2));
                btnDiscuss.setBackgroundColor(getColor(R.color.black2));
                btnPeople.setBackgroundColor(getColor(R.color.black));
                break;
        }
    }
}