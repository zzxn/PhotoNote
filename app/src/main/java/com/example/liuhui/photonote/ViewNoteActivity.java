package com.example.liuhui.photonote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class ViewNoteActivity extends AppCompatActivity {

    private ArrayList<String> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        paths = getIntent().getStringArrayListExtra("paths");
    }
}
