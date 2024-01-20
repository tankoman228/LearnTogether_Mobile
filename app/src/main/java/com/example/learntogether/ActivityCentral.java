package com.example.learntogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class ActivityCentral extends AppCompatActivity {

    ImageButton btnNews, btnFiles, btnMeetings, btnDiscuss, btnPeople;

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
    }

    private void switchMode(Mode i) {
        currentMode = i;
        refreshButtonsColor();
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