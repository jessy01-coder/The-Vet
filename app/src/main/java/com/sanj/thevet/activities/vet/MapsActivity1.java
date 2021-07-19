package com.sanj.thevet.activities.vet;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanj.thevet.R;
import com.sanj.thevet.databinding.ActivityMaps1Binding;


public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // add a marker where the farmer is located
        Bundle extras = getIntent().getExtras();
        String lat = extras.getString("lat");
        String lon = extras.getString("lon");
        String name = extras.getString("name");
        String phone = extras.getString("phone");
        LatLng sydney = new LatLng(Double.valueOf(lat),Double.valueOf(lon));
        mMap.addMarker(new MarkerOptions().position(sydney).title(name+ " " + phone));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
    }
}