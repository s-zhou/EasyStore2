package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_wait3s_activity);
        int tiempoEntrda = 2000;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ContinueWithActivity.class);
                startActivity(intent);
                finish();
                handler.removeCallbacks(null);
            }
        }, tiempoEntrda );//define el tiempo.
    }

}