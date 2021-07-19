package com.sanj.thevet.activities.farmer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.sanj.thevet.R;
import com.sanj.thevet.activities.About;
import com.sanj.thevet.activities.ResetPassword;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sanj.thevet.data.URLs.farmerUpdateDetailsUrl;
import static com.sanj.thevet.data.URLs.fetchFarmerProfileUrl;
import static com.sanj.thevet.wrapper.Wrapper.authenticatedNationalIdentificationNumber;

public class FarmerProfile extends AppCompatActivity {
    private TextView profile_init, txtName, groomCount, txtVaccinationCount, txtInseminationCount,
            txtDentistCount, txtNutritionCount, txtGeneralCount, farmerName, farmerPhone, farmerLocation, farmerNID;
    private String name, phone, nid, county, constituency, ward;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_profile);
        iniComponents();
    }

    private void iniComponents() {
        mContext = this;
        profile_init = findViewById(R.id.profile_init);
        txtName = findViewById(R.id.txtName);
        groomCount = findViewById(R.id.groomCount);
        txtVaccinationCount = findViewById(R.id.txtVaccinationCount);
        txtInseminationCount = findViewById(R.id.txtInseminationCount);
        txtDentistCount = findViewById(R.id.txtDentistCount);
        txtNutritionCount = findViewById(R.id.txtNutritionCount);
        txtGeneralCount = findViewById(R.id.txtGeneralCount);
        farmerName = findViewById(R.id.farmerName);
        farmerPhone = findViewById(R.id.farmerPhone);
        farmerLocation = findViewById(R.id.farmerLocation);
        farmerNID = findViewById(R.id.farmerNID);
        TextView txtChangePassword = findViewById(R.id.txtChangePassword);
        TextView txtAbout = findViewById(R.id.txtAbout);
        ImageButton btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> displayEditDialog());
        txtChangePassword.setOnClickListener(v -> displayChangePasswordDialog());
        txtAbout.setOnClickListener(v -> startActivity(new Intent(mContext, About.class)));
    }

    private void displayChangePasswordDialog() {
        Intent intent=new Intent(mContext, ResetPassword.class);
        intent.putExtra("auth",true);
        intent.putExtra("type","F1");
        startActivity(intent);
    }

    private void displayEditDialog() {
        String[] names = name.split(" ");
        @SuppressLint("InflateParams") View root = LayoutInflater.from(mContext).inflate(R.layout.update_farmer_dialog_item, null);
        TextInputEditText edFirstName, edSecondName, edIdNo, edPhone, edCounty, edConstituency, edWard;
        Button btnSubmit;
        edFirstName = root.findViewById(R.id.edFirstName);
        edSecondName = root.findViewById(R.id.edSecondName);
        edIdNo = root.findViewById(R.id.edIdNo);
        edPhone = root.findViewById(R.id.edPhone);
        edCounty = root.findViewById(R.id.edCounty);
        edConstituency = root.findViewById(R.id.edConstituency);
        edWard = root.findViewById(R.id.edWard);
        btnSubmit = root.findViewById(R.id.btnSubmit);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(mContext)
                .setView(root)
                .create();
        dialog.show();
        edFirstName.setText(names[0]);
        edSecondName.setText(names[1]);
        edIdNo.setText(nid);
        edPhone.setText(phone);
        edCounty.setText(county);
        edConstituency.setText(constituency);
        edWard.setText(ward);
        btnSubmit.setOnClickListener(v -> {
            if (!(TextUtils.isEmpty(Objects.requireNonNull(edFirstName.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edSecondName.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edIdNo.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edPhone.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edCounty.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edConstituency.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edWard.getText()).toString().trim()))) {
                dialog.dismiss();
                name = edFirstName.getText().toString() + " " + edSecondName.getText().toString();
                phone = edPhone.getText().toString();
                nid = edIdNo.getText().toString();
                county = edCounty.getText().toString();
                constituency = edConstituency.getText().toString();
                ward = edWard.getText().toString();

                updateAccountDetails();
            } else {
                new Wrapper().errorToast("Incomplete form!", mContext);
            }
        });
    }

    private void updateAccountDetails() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Updating farmer account", mContext);
        Runnable updateAccountDetailsThread = () -> {
            runOnUiThread(dialogWaiting::show);

            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("nid", nid);
            params.put("phone", phone);
            params.put("county", county);
            params.put("constituency", constituency);
            params.put("ward", ward);

            StringRequest request = new StringRequest(Request.Method.POST, farmerUpdateDetailsUrl, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String responseCode = responseObject.getString("responseCode");
                    String responseMessage = responseObject.getString("responseMessage");

                    if (responseCode.equals("1")) {
                        runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
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
        };
        new Thread(updateAccountDetailsThread).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Fetching Your profile", mContext);
        Runnable loadDataThread = () -> {
            runOnUiThread(dialogWaiting::show);
            HashMap<String, String> params = new HashMap<>();
            params.put("nid", authenticatedNationalIdentificationNumber);

            StringRequest request = new StringRequest(Request.Method.POST, fetchFarmerProfileUrl, response -> {
                try {
                    runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);

                    String responseCode = responseObject.getString("responseCode");
                    if (responseCode.equals("1")) {
                        String DCount = responseObject.getString("dentistCount"),
                                VCount = responseObject.getString("vaccinationCount"),
                                GenCount = responseObject.getString("generalCount"), GrCount = responseObject.getString("groomCount"), AICount = responseObject.getString("inseminationCount"), NCount = responseObject.getString("nutritionCount");
                        JSONArray responseArray = responseObject.getJSONArray("responseData");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject responseArrayObject = responseArray.getJSONObject(i);
                            String location;
                            name = responseArrayObject.getString("name");
                            location = responseArrayObject.getString("county") + " county, "
                                    + responseArrayObject.getString("constituency") + " constituency, "
                                    + responseArrayObject.getString("ward") + " ward";
                            phone = responseArrayObject.getString("phone");
                            nid = responseArrayObject.getString("nid");
                            county = responseArrayObject.getString("county");
                            constituency = responseArrayObject.getString("constituency");
                            ward = responseArrayObject.getString("ward");

                            runOnUiThread(() -> {
                                txtDentistCount.setText(DCount);
                                txtVaccinationCount.setText(VCount);
                                txtGeneralCount.setText(GenCount);
                                groomCount.setText(GrCount);
                                txtInseminationCount.setText(AICount);
                                txtNutritionCount.setText(NCount);
                                profile_init.setText(String.valueOf(name.charAt(0)));
                                farmerName.setText(name);
                                txtName.setText(name);
                                farmerPhone.setText(phone);
                                farmerLocation.setText(location);
                                farmerNID.setText(nid);
                            });

                        }

                    } else {
                        String responseMessage = responseObject.getString("responseMessage");
                        runOnUiThread(() -> new Wrapper().errorToast(responseMessage, mContext));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                    runOnUiThread(dialogWaiting::dismiss);
                    runOnUiThread(() -> new Wrapper().errorToast("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext));
                }

            }, error -> runOnUiThread(() -> {
                finish();
                new Wrapper().errorToast("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
                dialogWaiting.dismiss();
            })) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(loadDataThread).start();
    }
}