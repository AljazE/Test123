package com.example.aljaz.uvtest123;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public HashMap<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();

        String path = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");
        map.put(name, path);
    }

    public void startGame(View v){
        Intent myIntent = new Intent(this, GameActivity.class);
        myIntent.putExtra("hashMap", map);
        startActivity(myIntent);

    }

    public void settings(View V){
        startActivity(new Intent(MainActivity.this, AddImageActivity.class));
    }

    public void exit(View v){
        System.exit(0);

    }
}
