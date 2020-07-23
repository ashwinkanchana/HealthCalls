package com.ashwinkanchana.healthcalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import static com.ashwinkanchana.healthcalls.Constants.PACKAGE_NAME;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IS_LOGGED_IN;

public class SplashActivity extends AppCompatActivity {


    boolean isLoggedIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);

        isLoggedIn = prefs.getBoolean(PREF_KEY_IS_LOGGED_IN, false);

    }


    @Override
    protected void onStart() {
        super.onStart();
        run();
    }

    public void run() {
        if(isLoggedIn) {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }
}
