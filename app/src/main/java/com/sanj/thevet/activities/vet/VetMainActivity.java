package com.sanj.thevet.activities.vet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sanj.thevet.R;
import com.sanj.thevet.activities.Messages;
import com.sanj.thevet.activities.SplashScreen;
import com.sanj.thevet.adapters.AppointmentRecyclerViewAdapter;
import com.sanj.thevet.models.AppointmentModel;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanj.thevet.data.URLs.fetchVetAppointmentsUrl;
import static com.sanj.thevet.wrapper.Wrapper.authenticatedNationalIdentificationNumber;

public class VetMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_main);
        initComponents();
    }

    private void initComponents() {
        mContext=this;
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vet_home_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuProfile:
                startActivity(new Intent(mContext, VetProfile.class));
                break;

            case R.id.menuSignOut:
                signOut();
                break;
            case R.id.menuPreviousWork:
                startActivity(new Intent(mContext, WorkHistory.class));
                break;
            case R.id.menufarmerslocation:
                startActivity(new Intent(mContext,Locationdetails.class));
                break;
        }

        return true;
    }

    private void signOut() {
        getSharedPreferences("thevet",MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        startActivity(new Intent(mContext, SplashScreen.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Fetching Your Appointments", mContext);
        Runnable loadDataThread = () -> {
            runOnUiThread(dialogWaiting::show);
            HashMap<String,String> params=new HashMap<>();
            params.put("nid",authenticatedNationalIdentificationNumber);

            StringRequest request = new StringRequest(Request.Method.POST, fetchVetAppointmentsUrl, response -> {
                try {
                    runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);

                    String responseCode = responseObject.getString("responseCode");
                    List<AppointmentModel> appointmentModelList=new ArrayList<>();
                    if (responseCode.equals("1")){

                        JSONArray responseArray = responseObject.getJSONArray("responseData");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject responseArrayObject = responseArray.getJSONObject(i);
                            String name,phone,location,farmerNID,appointmentId,status,date,category;
                            name=responseArrayObject.getString("name");
                            location=responseArrayObject.getString("county")+" county, "
                                    +responseArrayObject.getString("constituency")+" constituency, "
                                    +responseArrayObject.getString("ward")+" ward";
                            phone=responseArrayObject.getString("phone");
                            farmerNID=responseArrayObject.getString("nid");
                            appointmentId=responseArrayObject.getString("id");
                            status=responseArrayObject.getString("status");
                            date=responseArrayObject.getString("date");
                            category=responseArrayObject.getString("category");

                            appointmentModelList.add(new AppointmentModel(name,phone,location,farmerNID,appointmentId,status,date,category));
                        }

                    }else{
                        String responseMessage = responseObject.getString("responseMessage");
                        runOnUiThread(() -> Toast.makeText(mContext, responseMessage, Toast.LENGTH_SHORT).show());
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    recyclerView.setAdapter(new AppointmentRecyclerViewAdapter(appointmentModelList,mContext,this));
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(dialogWaiting::dismiss);
                    runOnUiThread(() -> new Wrapper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage()+response, mContext));
                }

            }, error ->runOnUiThread(() -> {new Wrapper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
                dialogWaiting.dismiss();})){
                @Override
                protected Map<String, String> getParams(){
                    return params;
                }
            };
            Volley.newRequestQueue(mContext).add(request);
        };
        new Thread(loadDataThread).start();
    }
}