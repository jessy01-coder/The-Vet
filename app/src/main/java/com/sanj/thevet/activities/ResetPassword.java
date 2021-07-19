package com.sanj.thevet.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.sanj.thevet.R;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sanj.thevet.data.URLs.detailsVerificationUrl;
import static com.sanj.thevet.data.URLs.resetPasswordUrl;
import static com.sanj.thevet.wrapper.Wrapper.authenticatedNationalIdentificationNumber;

public class ResetPassword extends AppCompatActivity {
    private Context mContext;
    private String nid,password,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mContext=this;
        if (!getIntent().getExtras().getBoolean("auth",false)){
            displayVerificationDialog();
        }else{
            type=getIntent().getExtras().getString("type");
            nid=authenticatedNationalIdentificationNumber;
        }
        TextInputEditText edPassword=findViewById(R.id.edPassword);
        Button btnSubmit=findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            if (!(Objects.requireNonNull(edPassword.getText()).toString().isEmpty())){
                password=edPassword.getText().toString();
                updatePassword();
            }else{
                new Wrapper().errorToast("Please input the new Password",mContext);
            }
        });

    }

    private void updatePassword() {
        android.app.AlertDialog dialogWaiting = new Wrapper().waitingDialog("Verifying you details", mContext);
        Runnable updatePasswordThread = () -> {
            runOnUiThread(dialogWaiting::show);

            HashMap<String, String> params = new HashMap<>();
            params.put("nid", nid);
            params.put("password", password);
            params.put("type", type);

            StringRequest request = new StringRequest(Request.Method.POST, resetPasswordUrl, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String responseCode = responseObject.getString("responseCode");
                    String responseMessage = responseObject.getString("responseMessage");

                    if (responseCode.contains("1")) {
                        finish();
                        runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
                    } else {
                        runOnUiThread(() -> new Wrapper().errorToast(responseMessage, mContext));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        dialogWaiting.dismiss();
                        new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext);
                    });
                }
                runOnUiThread(dialogWaiting::dismiss);
            }, error -> runOnUiThread(() -> {
                dialogWaiting.dismiss();
                new Wrapper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
            })) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(updatePasswordThread).start();
    }

    private void displayVerificationDialog() {
        @SuppressLint("InflateParams") View root= LayoutInflater.from(mContext).inflate(R.layout.details_verification_item,null);
        TextInputEditText edIdNo=root.findViewById(R.id.edIdNo);
        TextInputEditText edPhone=root.findViewById(R.id.edPhone);
        Button btnVerify=root.findViewById(R.id.btnVerify);

        AlertDialog dialog=new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setView(root)
                .create();
        dialog.show();
        btnVerify.setOnClickListener(v -> {
            if (!(Objects.requireNonNull(edIdNo.getText()).toString().isEmpty() || Objects.requireNonNull(edPhone.getText()).toString().isEmpty())){
                dialog.dismiss();
                verifyDetails(edIdNo.getText().toString(),edPhone.getText().toString());
            }else{
                new Wrapper().errorToast("Incomplete details!",mContext);
            }
        });
    }

    private void verifyDetails(String mNID, String phone) {
        android.app.AlertDialog dialogWaiting = new Wrapper().waitingDialog("Verifying you details", mContext);
        Runnable verifyDetailsThread = () -> {
            runOnUiThread(dialogWaiting::show);

            HashMap<String, String> params = new HashMap<>();
            params.put("nid", mNID);
            params.put("phone", phone);

            StringRequest request = new StringRequest(Request.Method.POST, detailsVerificationUrl, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String responseCode = responseObject.getString("responseCode");
                    String responseMessage = responseObject.getString("responseMessage");

                    if (responseCode.contains("1")) {
                        type=responseCode;
                        nid=mNID;
                        runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
                    } else {
                        finish();
                        runOnUiThread(() -> new Wrapper().errorToast(responseMessage, mContext));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        finish();
                        dialogWaiting.dismiss();
                        new Wrapper().errorToast("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext);
                    });
                }
                runOnUiThread(dialogWaiting::dismiss);
            }, error -> runOnUiThread(() -> {
                finish();
                dialogWaiting.dismiss();
                new Wrapper().errorToast("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
            })) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(verifyDetailsThread).start();
    }


}