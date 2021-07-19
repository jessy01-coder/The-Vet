package com.sanj.thevet.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sanj.thevet.R;
import com.sanj.thevet.models.AppointmentModel;
import com.sanj.thevet.wrapper.Wrapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanj.thevet.data.URLs.vetAppointmentFeedbackUrl;

public class MyBookingsRecyclerViewAdapter extends RecyclerView.Adapter<MyBookingsRecyclerViewAdapter.ViewHolder> {
    private List<AppointmentModel> appointmentModelList;
    private Context mContext;
    private Activity mActivity;

    public MyBookingsRecyclerViewAdapter(List<AppointmentModel> appointmentModelList, Context mContext, Activity mActivity) {
        this.appointmentModelList=appointmentModelList;
        this.mContext=mContext;
        this.mActivity=mActivity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_bookings_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyBookingsRecyclerViewAdapter.ViewHolder holder, int position) {
        AppointmentModel model=appointmentModelList.get(position);
        holder.txtDate.setText(model.getDate());
        holder.txtPhone.setText(model.getPhone());
        holder.txtCategory.setText(model.getCategory());

        switch (model.getStatus()) {
            case "1":
                holder.txtPending.setVisibility(View.GONE);
                holder.txtAccepted.setVisibility(View.GONE);
                holder.txtComplete.setVisibility(View.VISIBLE);
                break;
            case "0":
                holder.txtPending.setVisibility(View.VISIBLE);
                holder.txtAccepted.setVisibility(View.GONE);
                holder.txtComplete.setVisibility(View.GONE);
                break;
            case "00":
                holder.txtPending.setVisibility(View.GONE);
                holder.txtAccepted.setVisibility(View.VISIBLE);
                holder.txtComplete.setVisibility(View.GONE);
                break;
        }

    }


    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate,txtPhone,txtCategory,txtPending,txtAccepted,txtComplete;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtPhone=itemView.findViewById(R.id.txtPhone);
            txtCategory=itemView.findViewById(R.id.txtCategory);
            txtPending=itemView.findViewById(R.id.txtPending);
            txtAccepted=itemView.findViewById(R.id.txtAccepted);
            txtComplete=itemView.findViewById(R.id.txtComplete);
        }
    }
}
