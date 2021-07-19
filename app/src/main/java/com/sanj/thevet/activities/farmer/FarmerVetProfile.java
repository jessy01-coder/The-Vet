package com.sanj.thevet.activities.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.sanj.thevet.R;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.sanj.thevet.data.URLs.bookAppointmentUrl;
import static com.sanj.thevet.data.URLs.fetchVetProfileUrl;
import static com.sanj.thevet.wrapper.Wrapper.authenticatedNationalIdentificationNumber;

public class FarmerVetProfile extends AppCompatActivity {
    private String vetNID,vetCategoryStr;
    private Context mContext;
    private TextView profile_init, vetName, vetPhone, vetLocation, vetLicenseNumber, vetCategory, vetBriefExplanation;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_vet_profile);
        initComponents();
    }

    private void initComponents() {
        vetNID = getIntent().getExtras().getString("nid");
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        profile_init = findViewById(R.id.profile_init);
        vetName = findViewById(R.id.vetName);
        vetPhone = findViewById(R.id.vetPhone);
        vetLocation = findViewById(R.id.vetLocation);
        vetLicenseNumber = findViewById(R.id.vetLicenseNumber);
        vetCategory = findViewById(R.id.vetCategory);
        vetBriefExplanation = findViewById(R.id.vetBriefExplanation);
        Button btnHireMe = findViewById(R.id.btnHireMe);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        btnHireMe.setOnClickListener(v -> hireVet());

    }

    private void hireVet() {
        Calendar calendar=Calendar.getInstance();
        String[] months=getResources().getStringArray(R.array.months);
        String month=months[calendar.get(Calendar.MONTH)];
        String date=calendar.get(Calendar.DAY_OF_MONTH)+" "+month+" "+calendar.get(Calendar.YEAR);
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Booking appointment with vet", mContext);
        Runnable bookAppointmentThread = () -> {
            runOnUiThread(dialogWaiting::show);
            HashMap<String, String> params = new HashMap<>();
            params.put("vetNID", vetNID);
            params.put("farmerNID",authenticatedNationalIdentificationNumber);
            params.put("category",vetCategoryStr);
            params.put("date",date);

            StringRequest request = new StringRequest(Request.Method.POST, bookAppointmentUrl, response -> {
                try {
                    runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);
                    String responseCode = responseObject.getString("responseCode");
                    String responseMessage = responseObject.getString("responseMessage");

                    if (responseCode.equals("1")) {
                        runOnUiThread(() -> new Wrapper().successToast(responseMessage, mContext));
                        finish();
                    } else {
                        runOnUiThread(() -> new Wrapper().messageDialog(responseMessage, mContext));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(dialogWaiting::dismiss);
                    runOnUiThread(() -> new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext));
                }

            }, error -> runOnUiThread(() -> {
                new Wrapper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
                dialogWaiting.dismiss();
            })) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(bookAppointmentThread).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }
    private void loadData() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Fetching Vet profile", mContext);
        Runnable loadDataThread = () -> {
            runOnUiThread(dialogWaiting::show);
            HashMap<String, String> params = new HashMap<>();
            params.put("nid", vetNID);

            StringRequest request = new StringRequest(Request.Method.POST, fetchVetProfileUrl, response -> {
                try {
                    runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);

                    String responseCode = responseObject.getString("responseCode");
                    if (responseCode.equals("1")) {
                        JSONArray responseArray = responseObject.getJSONArray("responseData");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject responseArrayObject = responseArray.getJSONObject(i);
                            String name, phone, location, licenseNumber,explanation;
                            name = responseArrayObject.getString("name");
                            location = responseArrayObject.getString("county") + " county, "
                                    + responseArrayObject.getString("constituency") + " constituency, "
                                    + responseArrayObject.getString("ward") + " ward";
                            phone = responseArrayObject.getString("phone");
                            vetCategoryStr = responseArrayObject.getString("category");
                            licenseNumber = responseArrayObject.getString("license_number");
                            explanation= responseArrayObject.getString("explanation");

                            runOnUiThread(() -> {
                                collapsingToolbar.setTitle(name);
                                profile_init.setText(String.valueOf(name.charAt(0)));
                                vetName.setText(name);
                                vetPhone.setText(phone);
                                vetLocation.setText(location);
                                vetLicenseNumber.setText(licenseNumber);
                                vetCategory.setText(vetCategoryStr);
                                vetBriefExplanation.setText(explanation);
                            });

                        }

                    } else {
                        String responseMessage = responseObject.getString("responseMessage");
                        runOnUiThread(() -> new Wrapper().errorToast(responseMessage, mContext));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(dialogWaiting::dismiss);
                    runOnUiThread(() -> new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext));
                }

            }, error -> runOnUiThread(() -> {
                new Wrapper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
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