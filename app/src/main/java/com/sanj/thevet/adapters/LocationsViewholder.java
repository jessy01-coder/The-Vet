package com.sanj.thevet.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanj.thevet.R;

import org.jetbrains.annotations.NotNull;

import java.text.BreakIterator;

public class LocationsViewholder extends RecyclerView.ViewHolder {
    public TextView latitude,longitude;
    public Button findfarmer;
    public LocationsViewholder(@NonNull @NotNull View itemView) {
        super(itemView);
        latitude= itemView.findViewById(R.id.samplelat);
        longitude = itemView.findViewById(R.id.samplelon);
        findfarmer = itemView.findViewById(R.id.findfarmer);

    }
}
