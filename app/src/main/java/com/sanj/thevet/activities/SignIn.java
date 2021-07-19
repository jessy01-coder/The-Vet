package com.sanj.thevet.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sanj.thevet.R;
import com.sanj.thevet.activities.farmer.FarmerMainActivity;
import com.sanj.thevet.activities.farmer.FarmerSignUp;
import com.sanj.thevet.activities.vet.VetMainActivity;
import com.sanj.thevet.activities.vet.VetSignUp;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sanj.thevet.data.URLs.authenticationUrl;

public class SignIn extends AppCompatActivity {
    private Context mContext;
    private EditText edIdNo,edPassword;
    private SharedPreferences.Editor editor;
    private CheckBox keep_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
       initComponents();
    }

    @SuppressLint("CommitPrefEdits")
    private void initComponents() {
        mContext =this;
        TextView txtToCreateAccount = findViewById(R.id.to_create_account);
        txtToCreateAccount.setOnClickListener(v -> displayUserSignInChoice());

        edIdNo=findViewById(R.id.edIdNo);
        edPassword=findViewById(R.id.edPassword);
        keep_in=findViewById(R.id.keep_in);
        TextView to_reset=findViewById(R.id.to_reset);
        to_reset.setOnClickListener(v -> {
            Intent intent=new Intent(mContext, ResetPassword.class);
            intent.putExtra("auth",false);
            startActivity(intent);
        });
        Button btnSignIn = findViewById(R.id.sign_in);

        btnSignIn.setOnClickListener(v -> authenticateUser());
        editor=getSharedPreferences("thevet",MODE_PRIVATE).edit();
    }

    private void authenticateUser() {
        android.app.AlertDialog dialogWaiting=new Wrapper().waitingDialog("User validation and verification",mContext);
        Runnable authenticationThread = () -> {
            if (!(TextUtils.isEmpty(Objects.requireNonNull(edIdNo.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edPassword.getText()).toString().trim()))){

                    runOnUiThread(dialogWaiting::show);
                    String nid,password;
                    nid= Objects.requireNonNull(edIdNo.getText()).toString().trim();
                    password= Objects.requireNonNull(edPassword.getText()).toString().trim();

                    HashMap<String,String> params=new HashMap<>();
                    params.put("nid",nid);
                    params.put("password",password);

                    StringRequest request = new StringRequest(Request.Method.POST, authenticationUrl, response -> {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String responseCode = responseObject.getString("responseCode");
                            String responseMessage = responseObject.getString("responseMessage");

                            if (responseCode.contains("1")) {

                                Wrapper.authenticatedNationalIdentificationNumber=nid;
                                runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
                                if (responseCode.equals("V1")){
                                    editor.putBoolean("vet",true);
                                    startActivity(new Intent(mContext, VetMainActivity.class));
                                }else{
                                    editor.putBoolean("vet",false);
                                    startActivity(new Intent(mContext, FarmerMainActivity.class));
                                }
                                if (keep_in.isChecked()){
                                    editor.putBoolean("alreadyIn",true);
                                    editor.putString("nid",nid);
                                    editor.apply();
                                }
                                finish();
                            } else {
                                runOnUiThread(() -> new Wrapper().messageDialog(responseMessage, mContext));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                dialogWaiting.dismiss();
                                new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage()+response, mContext);
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
            }else{
                runOnUiThread(() -> new Wrapper().errorToast("All fields required!",mContext));
            }
        };
        new Thread(authenticationThread).start();
    }

    private void displayUserSignInChoice() {
        String[] userChoices=new String[]{"Veterinary","Farmer"};
        new AlertDialog.Builder(mContext)
                .setTitle("CREATE ACCOUNT AS:")
                .setSingleChoiceItems(userChoices, 0, (dialog, which) -> {
                    switch (which){
                        case 0:
                            startActivity(new Intent(mContext, VetSignUp.class));
                            break;
                        case 1:
                            startActivity(new Intent(mContext, FarmerSignUp.class));
                            break;
                    }
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}