package com.sanj.thevet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sanj.thevet.R;
import com.sanj.thevet.activities.farmer.FarmerMainActivity;
import com.sanj.thevet.activities.vet.VetMainActivity;
import com.sanj.thevet.wrapper.Wrapper;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {
    private View bullet1,bullet2,bullet3;
    private Animation animation;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext=this;
        bullet1=findViewById(R.id.bullet1);
        bullet2=findViewById(R.id.bullet2);
        bullet3=findViewById(R.id.bullet3);
        animation= AnimationUtils.loadAnimation(mContext,R.anim.blink);


        Runnable animationThread= () -> {
            try {
                bullet1.startAnimation(animation);
                sleep(100);
                bullet2.startAnimation(animation);
                sleep(100);
                bullet3.startAnimation(animation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(animationThread).start();

        Runnable splashThread= () -> {
            try {
                sleep(3000);
                SharedPreferences sharedPreferences= getSharedPreferences("thevet",MODE_PRIVATE);
                if (sharedPreferences.getBoolean("alreadyIn",false)){
                    Wrapper.authenticatedNationalIdentificationNumber=sharedPreferences.getString("nid","");
                    if (sharedPreferences.getBoolean("vet",false)){
                        startActivity(new Intent(mContext, VetMainActivity.class));
                    }else{
                        startActivity(new Intent(mContext, FarmerMainActivity.class));
                    }

                }else{
                    startActivity(new Intent(mContext,SignIn.class));
                }
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(splashThread).start();
    }
}