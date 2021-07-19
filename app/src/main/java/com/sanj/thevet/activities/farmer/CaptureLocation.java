package com.sanj.thevet.activities.farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanj.thevet.R;
import com.sanj.thevet.activities.SignIn;
import com.sanj.thevet.models.Loc;

import org.jetbrains.annotations.NotNull;

public class CaptureLocation extends AppCompatActivity {
Button mylocation;
FirebaseDatabase mydatabase;
private static final int REQUEST_LOCATION = 1;
ProgressBar pro;
Toolbar toolbar;
Double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_location);
        mylocation = findViewById(R.id.mylocation);
        mydatabase = FirebaseDatabase.getInstance();
        pro = findViewById(R.id.progress);
        toolbar = findViewById(R.id.yourlocations);
        getActionBar();
        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CaptureLocation.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

                }
                else
                {
                    getCurrentlocation();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_LOCATION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getCurrentlocation();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),"permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("MissingPermission")
    private void getCurrentlocation()

    {
        pro.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(CaptureLocation.this).
                requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(CaptureLocation.this).removeLocationUpdates(this);
                        if(locationResult !=null && locationResult.getLocations().size()>0 )
                        {
                            int latestlocationindex = locationResult.getLocations().size()-1;
                            double lat = locationResult.getLocations().get(latestlocationindex).getLatitude();
                            double lon = locationResult.getLocations().get(latestlocationindex).getLongitude();
                            Intent intent = getIntent();
                            String name = intent.getStringExtra("name");
                            String phone = intent.getStringExtra("phone");
                            Loc loc = new Loc(String.valueOf(lat),
                                    String.valueOf(lon),
                                    name,
                                   phone);
                            FirebaseDatabase mydb = FirebaseDatabase.getInstance();
                            DatabaseReference myref = FirebaseDatabase.getInstance().getReference().child("farmers");
                            myref.child(myref.push().getKey()).setValue(loc);

                            Toast.makeText(getApplicationContext(),"Please sign in ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplication(),SignIn.class));

                        }
                        pro.setVisibility(View.GONE);
                    }

                },Looper.getMainLooper());


    }

}