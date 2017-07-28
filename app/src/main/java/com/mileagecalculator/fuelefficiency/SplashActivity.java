package com.mileagecalculator.fuelefficiency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mileagecalculator.R;

@SuppressWarnings("SameParameterValue")
public class SplashActivity extends AppCompatActivity {
    private String id_shr;
    private boolean isFirstImage = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
        id_shr = prefs.getString("id", null);
        openNextActivity();
    }

    private void openNextActivity() {
        int SPLASH_DISPLAY_LENGHT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id_shr != null) {
                    Intent i = new Intent(SplashActivity.this, GraphViewShow.class);
                    SplashActivity.this.startActivity(i);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent i = new Intent(SplashActivity.this, Registration.class);
                    i.putExtra(FuelEntry.Flag, "1");
                    SplashActivity.this.startActivity(i);
                    SplashActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}