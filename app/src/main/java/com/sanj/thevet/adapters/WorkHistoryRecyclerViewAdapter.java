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
import com.sanj.thevet.activities.vet.WorkHistory;
import com.sanj.thevet.models.AppointmentModel;
import com.sanj.thevet.wrapper.Wrapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static com.sanj.thevet.data.URLs.vetAppointmentFeedbackUrl;

public class WorkHistoryRecyclerViewAdapter extends RecyclerView.Adapter<WorkHistoryRecyclerViewAdapter.ViewHolder> {
    private List<AppointmentModel> appointmentModelList;
    private Context mContext;
    private Activity mActivity;

    public WorkHistoryRecyclerViewAdapter(List<AppointmentModel> appointmentModelList, Context mContext, Activity mActivity) {
        this.appointmentModelList=appointmentModelList;
        this.mContext=mContext;
        this.mActivity=mActivity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.work_history_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WorkHistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        AppointmentModel model=appointmentModelList.get(position);
        holder.txtDate.setText(model.getDate());
        holder.txtPhone.setText(model.getPhone());
        holder.txtLocation.setText(model.getLocation());
        holder.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeAppointment(model.getAppointmentId());
            }
        });

        if (model.getStatus().equals("1")){
            holder.btnComplete.setVisibility(View.GONE);
            holder.imageComplete.setVisibility(View.VISIBLE);
        }else{
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.imageComplete.setVisibility(View.GONE);
        }

    }

    private void completeAppointment(String appointmentId) {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Completing appointment", mContext);
        Runnable sendFeedbackThread = () -> {
            mActivity.runOnUiThread(dialogWaiting::show);
            HashMap<String,String> params=new HashMap<>();
            params.put("id",appointmentId);
            params.put("feedback","complete");

            StringRequest request = new StringRequest(Request.Method.POST, vetAppointmentFeedbackUrl, response -> {
                try {
                    mActivity.runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);

                    String responseCode = responseObject.getString("responseCode");
                    String responseMessage = responseObject.getString("responseMessage");

                    if (responseCode.equals("1")){
                        mActivity.runOnUiThread(() -> new Wrapper().successToast(responseMessage,mContext));
                    }else{
                        mActivity.runOnUiThread(() -> new Wrapper().messageDialog(responseMessage,mContext));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(dialogWaiting::dismiss);
                    mActivity.runOnUiThread(() -> new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage()+response, mContext));
                }

            }, error ->mActivity.runOnUiThread(() -> {new Wrapper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
                dialogWaiting.dismiss();})){
                @Override
                protected Map<String, String> getParams(){
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(sendFeedbackThread).start();
    }

    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate,txtPhone,txtLocation,btnComplete;
        ImageView imageComplete;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtPhone=itemView.findViewById(R.id.txtPhone);
            txtLocation=itemView.findViewById(R.id.txtLocation);
            btnComplete=itemView.findViewById(R.id.btnComplete);
            imageComplete=itemView.findViewById(R.id.imageComplete);
        }
    }
}
