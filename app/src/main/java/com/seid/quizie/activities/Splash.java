package com.seid.quizie.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.seid.quizie.MainActivity;
import com.seid.quizie.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        new Handler().postDelayed(() -> {
            if (sp.getBoolean("auth", false)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, Login.class));
                finish();
            }
        }, 3000);
    }
}