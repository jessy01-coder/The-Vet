package com.sanj.thevet.activities.farmer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sanj.thevet.R;
import com.sanj.thevet.adapters.VetRecyclerViewAdapter;
import com.sanj.thevet.models.VetModel;
import com.sanj.thevet.wrapper.Wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanj.thevet.data.URLs.fetchVetByCategoryUrl;

public class FarmerList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context mContext;
    private String vetCategory;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_list);
        initComponents();
    }

    private void initComponents() {
        mContext=this;
         toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView=findViewById(R.id.recyclerView);

        vetCategory=getIntent().getExtras().getString("category");
        toolbar.setTitle(vetCategory);


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        AlertDialog dialogWaiting = new Wrapper().waitingDialog("Fetching Vet(s)", mContext);
        Runnable loadDataThread = () -> {
            runOnUiThread(dialogWaiting::show);
            HashMap<String,String> params=new HashMap<>();
            params.put("category",vetCategory);

            StringRequest request = new StringRequest(Request.Method.POST, fetchVetByCategoryUrl, response -> {
                try {
                    runOnUiThread(dialogWaiting::dismiss);
                    JSONObject responseObject = new JSONObject(response);

                    String responseCode = responseObject.getString("responseCode");
                    if (responseCode.equals("1")){
                        List<VetModel> vetModelList=new ArrayList<>();
                        JSONArray responseArray = responseObject.getJSONArray("responseData");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject responseArrayObject = responseArray.getJSONObject(i);
                            String name,phone,location,licenseNumber,category,nid;
                            name=responseArrayObject.getString("name");
                            location=responseArrayObject.getString("county")+" county, "
                                    +responseArrayObject.getString("constituency")+" constituency, "
                                    +responseArrayObject.getString("ward")+" ward";
                            phone=responseArrayObject.getString("phone");
                            category=responseArrayObject.getString("category");
                            licenseNumber=responseArrayObject.getString("license_number");
                            nid=responseArrayObject.getString("nid");

                            vetModelList.add(new VetModel(name,phone,location,licenseNumber,category,nid));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(new VetRecyclerViewAdapter(vetModelList,mContext));
                    }else{
                        String responseMessage = responseObject.getString("responseMessage");
                        runOnUiThread(() -> Toast.makeText(mContext, responseMessage, Toast.LENGTH_SHORT).show());
                    }
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