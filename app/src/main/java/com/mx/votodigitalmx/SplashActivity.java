package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // We create a new Thread
        Thread splashThread = new Thread() {
            @Override
            public void run () {
                try{
                    sleep(3000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // Start Thread
        splashThread.start();
    }
}
