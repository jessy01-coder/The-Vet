package com.sanj.thevet.activities.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.widget.ProgressBar;

import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.textfield.TextInputEditText;
import com.sanj.thevet.R;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sanj.thevet.data.URLs.*;

public class FarmerSignUp extends AppCompatActivity {
    private TextInputEditText edFirstName, edSecondName, edIdNo, edPhone, edCounty, edConstituency, edWard, edPassword, edConfirmPassword;
    private Context mContext;
    Double lat,lon;
    FirebaseDatabase mydatabase;
    ProgressBar pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_sign_up);
        initComponents();
    }

    private void initComponents() {
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        edFirstName = findViewById(R.id.edFirstName);
        edSecondName = findViewById(R.id.edSecondName);
        edIdNo = findViewById(R.id.edIdNo);
        edPhone = findViewById(R.id.edPhone);
        edCounty = findViewById(R.id.edCounty);
        edConstituency = findViewById(R.id.edConstituency);
        edWard = findViewById(R.id.edWard);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        pro = findViewById(R.id.progress);
        mydatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuSave) {
            register();
        }
        return true;
    }

    private void register() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Creating farmer account", mContext);
        Runnable registrationThread = () -> {
            if (!(TextUtils.isEmpty(Objects.requireNonNull(edFirstName.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edSecondName.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edIdNo.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edPhone.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edCounty.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edConstituency.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edWard.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edPassword.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edConfirmPassword.getText()).toString().trim()))) {

                if (edPassword.getText().toString().trim().equals(edConfirmPassword.getText().toString().trim())) {
                    runOnUiThread(dialogWaiting::show);
                    String name, nid, phone, county, constituency, ward, password;
                    name = Objects.requireNonNull(edFirstName.getText()).toString().trim() + " " + Objects.requireNonNull(edSecondName.getText()).toString().trim();
                    phone = Objects.requireNonNull(edPhone.getText()).toString().trim();
                    nid = Objects.requireNonNull(edIdNo.getText()).toString().trim();
                    county = Objects.requireNonNull(edCounty.getText()).toString().trim();
                    constituency = Objects.requireNonNull(edConstituency.getText()).toString().trim();
                    ward = Objects.requireNonNull(edWard.getText()).toString().trim();
                    password = Objects.requireNonNull(edPassword.getText()).toString().trim();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("nid", nid);
                    params.put("phone", phone);
                    params.put("county", county);
                    params.put("constituency", constituency);
                    params.put("ward", ward);
                    params.put("password", password);

                    StringRequest request = new StringRequest(Request.Method.POST, farmerRegistrationUrl, response -> {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String responseCode = responseObject.getString("responseCode");
                            String responseMessage = responseObject.getString("responseMessage");

                            if (responseCode.equals("1")) {
                                runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
                               // startActivity(new Intent(getApplicationContext(),CaptureLocation.class));
                                Intent intent = new Intent(getApplicationContext(),CaptureLocation.class);
                                intent.putExtra("name",name);
                                intent.putExtra("phone",phone);
                                startActivity(intent);
                            } else {
                                runOnUiThread(() -> new Wrapper().messageDialog(responseMessage, mContext));
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
                } else {
                    runOnUiThread(() -> new Wrapper().errorToast("Passwords do not match!", mContext));
                }
            } else {
                runOnUiThread(() -> new Wrapper().errorToast("Incomplete registration form!", mContext));
            }
        };
        new Thread(registrationThread).start();
    }

}