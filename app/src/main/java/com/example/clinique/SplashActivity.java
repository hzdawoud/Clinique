package com.example.clinique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent splashIntent = new Intent( SplashActivity.this, LoginActivity.class);
                        startActivity(splashIntent);
                        finish();
                    }
                }
                , SPLASH_TIME_OUT);

    }
}
