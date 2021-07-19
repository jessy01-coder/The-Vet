package com.sanj.thevet.activities.farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sanj.thevet.R;
import com.sanj.thevet.activities.Messages;
import com.sanj.thevet.activities.SignIn;
import com.sanj.thevet.activities.SplashScreen;

public class FarmerMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_main);
        initComponents();
    }

    private void initComponents() {
        mContext=this;
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CardView cardGroom = findViewById(R.id.cardGroom);
        CardView cardVaccination = findViewById(R.id.cardVaccination);
        CardView cardArtificialInsemination = findViewById(R.id.cardArtificialInsemination);
        CardView cardDentist = findViewById(R.id.cardDentist);
        CardView cardNutrition = findViewById(R.id.cardNutrition);
        CardView cardGeneralPhysician = findViewById(R.id.cardGeneralPhysician);

        cardGroom.setOnClickListener(this);
        cardVaccination.setOnClickListener(this);
        cardArtificialInsemination.setOnClickListener(this);
        cardDentist.setOnClickListener(this);
        cardNutrition.setOnClickListener(this);
        cardGeneralPhysician.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.farmer_home_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuProfile:
                startActivity(new Intent(mContext,FarmerProfile.class));
                break;

            case R.id.menuBookings:
                startActivity(new Intent(mContext, MyBookedAppointments.class));
                break;
            case R.id.menuSignOut:
                signOut();
                break;
        }

        return true;
    }

    private void signOut() {
        getSharedPreferences("thevet",MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        startActivity(new Intent(mContext, SplashScreen.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mContext,FarmerList.class);
        switch (v.getId()){
            case R.id.cardGroom:
                intent.putExtra("category","Groom");
                startActivity(intent);
                break;
            case R.id.cardArtificialInsemination:
                intent.putExtra("category","Artificial Insemination");
                startActivity(intent);
                break;
            case R.id.cardDentist:
                intent.putExtra("category","Dentist");
                startActivity(intent);
                break;
            case R.id.cardGeneralPhysician:
                intent.putExtra("category","General Physician");
                startActivity(intent);
                break;
            case R.id.cardNutrition:
                intent.putExtra("category","Nutrition");
                startActivity(intent);
                break;
            case R.id.cardVaccination:
                intent.putExtra("category","Vaccination");
                startActivity(intent);
                break;
        }
    }
}