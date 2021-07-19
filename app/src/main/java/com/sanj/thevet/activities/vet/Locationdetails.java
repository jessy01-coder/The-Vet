package com.sanj.thevet.activities.vet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanj.thevet.R;
import com.sanj.thevet.adapters.LocationsViewholder;
import com.sanj.thevet.models.Loc;

import org.jetbrains.annotations.NotNull;

public class Locationdetails extends AppCompatActivity {
RecyclerView myrecycler;
    FirebaseRecyclerAdapter<Loc, LocationsViewholder> adapter;
    FirebaseRecyclerOptions<Loc> options ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationdetails);
        myrecycler = findViewById(R.id.myrecycler);
        LinearLayoutManager l = new LinearLayoutManager(Locationdetails.this);
        myrecycler.setLayoutManager(l);
        myrecycler.setHasFixedSize(true);
        fetch();
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        getActionBar();

    }
    private void fetch()
    {

        DatabaseReference myref = FirebaseDatabase.getInstance().getReference().child("farmers");
        options = new FirebaseRecyclerOptions.Builder<Loc>().setQuery(myref, Loc.class).build();
        adapter = new FirebaseRecyclerAdapter<Loc, LocationsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull LocationsViewholder holder, int position, @NonNull @NotNull Loc model) {
               holder.latitude.setText(model.getLatitude());
               holder.longitude.setText(model.getLongitude());
               holder.findfarmer.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                      Intent i = new Intent(getApplicationContext(),MapsActivity1.class);
                      i.putExtra("lat",model.getLatitude());
                      i.putExtra("lon",model.getLongitude());
                      i.putExtra("phone",model.getPhone());
                      i.putExtra("name",model.getUid());
                      startActivity(i);
                   }
               });

            }

            @NonNull
            @NotNull
            @Override
            public LocationsViewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.samplelocations,parent,false);
                return  new LocationsViewholder(view);
            }
        };
        adapter.startListening();
        myrecycler.setAdapter(adapter);

    }
}