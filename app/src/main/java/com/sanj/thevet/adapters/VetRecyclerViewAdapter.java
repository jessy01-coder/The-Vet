package com.sanj.thevet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanj.thevet.R;
import com.sanj.thevet.activities.farmer.FarmerVetProfile;
import com.sanj.thevet.models.VetModel;

import java.util.List;

public class VetRecyclerViewAdapter extends RecyclerView.Adapter<VetRecyclerViewAdapter.ViewHolder> {
    private final List<VetModel> vetModelList;
    private Context mContext;

    public VetRecyclerViewAdapter(List<VetModel> vetModelList, Context mContext) {
        this.vetModelList = vetModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VetRecyclerViewAdapter.ViewHolder holder, int position) {
        VetModel model=vetModelList.get(position);
        holder.name.setText(model.getName());
        holder.category.setText(model.getCategory());
        holder.phone.setText(model.getPhone());
        holder.location.setText(model.getLocation());
        holder.licenseNumber.setText(model.getLicenseNumber());
        holder.btnPreview.setOnClickListener(v -> {
            Intent intent=new Intent(mContext, FarmerVetProfile.class);
            intent.putExtra("nid",model.getNid());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vetModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,phone,location,licenseNumber,category;
        Button btnPreview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.vetName);
            phone=itemView.findViewById(R.id.vetPhone);
            location=itemView.findViewById(R.id.vetLocation);
            licenseNumber=itemView.findViewById(R.id.vetLicenseNumber);
            category=itemView.findViewById(R.id.vetCategory);
            btnPreview=itemView.findViewById(R.id.btnPreview);
        }
    }
}
