package com.sanj.thevet.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sanj.thevet.R;
import com.sanj.thevet.models.AppointmentModel;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanj.thevet.data.URLs.vetAppointmentFeedbackUrl;

public class AppointmentRecyclerViewAdapter extends RecyclerView.Adapter<AppointmentRecyclerViewAdapter.ViewHolder> {

    private final List<AppointmentModel> appointmentModelList;
    private final Context mContext;
    private final Activity mActivity;
    private String mOrderId;

    public AppointmentRecyclerViewAdapter(List<AppointmentModel> appointmentModelList, Context mContext,Activity mActivity) {
        this.appointmentModelList = appointmentModelList;
        this.mContext = mContext;
        this.mActivity=mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentRecyclerViewAdapter.ViewHolder holder, int position) {
        AppointmentModel model=appointmentModelList.get(position);
        holder.farmerPhone.setText(model.getPhone());
        holder.farmerName.setText(model.getName());
        holder.farmerLocation.setText(model.getLocation());
        holder.btnDecline.setOnClickListener(v -> sendFeedback("decline"));
        holder.btnAccept.setOnClickListener(v -> sendFeedback("accept"));
        mOrderId=model.getAppointmentId();
    }

    private void sendFeedback(String feedBack) {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Sending feedback", mContext);
        Runnable sendFeedbackThread = () -> {
            mActivity.runOnUiThread(dialogWaiting::show);
            HashMap<String,String> params=new HashMap<>();
            params.put("id",mOrderId);
            params.put("feedback",feedBack);

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

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        TextView farmerName,farmerPhone,farmerLocation;
        Button btnAccept,btnDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            farmerLocation=itemView.findViewById(R.id.farmerLocation);
            farmerName=itemView.findViewById(R.id.farmerName);
            farmerPhone=itemView.findViewById(R.id.farmerPhone);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnDecline=itemView.findViewById(R.id.btnDecline);
        }
    }
}
