package com.example.employeeshuttlereport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PassengerManagement extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    ListView passengerListView;
    ListAdapter adapter;
    ProgressDialog loading;
    ArrayList<HashMap<String, String>> passengerList;
    Button buttonAddPassenger;
    String getUrl = "https://script.google.com/macros/s/AKfycbyImjCwgc9ZeJL4Al979ItmwlgFdNSyrLnwu2NlCIPvBDdoKl8/exec?action=getPassengers";
    String adminPermission;
    String language = Locale.getDefault().getDisplayLanguage();
    Bundle bundle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_passenger);

        Intent intent = getIntent();
        adminPermission = intent.getStringExtra("administratorPermission");

        initialize();

        getPassengers();

    }

    protected void initialize(){
        passengerListView = (ListView) findViewById(R.id.lv_passengers);
        passengerListView.setOnItemClickListener(this);

        buttonAddPassenger = (Button)findViewById(R.id.passengerManagement_addPassenger_button);
        buttonAddPassenger.setOnClickListener(this);
    }

    protected void getPassengers() {

        if(language.equalsIgnoreCase("English")) {
            loading = ProgressDialog.show(this,"Loading","Please wait");
        }
        else {
            loading = ProgressDialog.show(this,"טוען","אנא המתן");
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parsePassengers(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    protected void parsePassengers(String jsonResponse) {

        passengerList = new ArrayList<>();


        createPassengersList(jsonResponse);

        adapter = new SimpleAdapter(this,passengerList,R.layout.list_passenger_row,
                new String[]{"passengerNameInEnglish","passengerNameInHebrew","passengerPhoneNumber","passengerAddress"},new int[]{R.id.passengerNameInEnglish, R.id.passengerNameInHebrew, R.id.passengerPhoneNumber, R.id.passengerAddress});


        passengerListView.setAdapter(adapter);
        loading.dismiss();
    }

    @Override
    public void onClick(View v) {

        if (v == buttonAddPassenger) {

            Intent intent = new Intent(getApplicationContext(), Passenger.class);
            bundle = new Bundle(getIntent().getExtras());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Passenger.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String passengerNameInEnglish = map.get("passengerNameInEnglish").toString();
        String passengerNameInHebrew = map.get("passengerNameInHebrew").toString();
        String passengerPhoneNumber = map.get("passengerPhoneNumber").toString();
        String passengerAddress = map.get("passengerAddress").toString();

        // String sno = map.get("sno").toString();

        // Log.e("SNO test",sno);

        bundle = new Bundle(getIntent().getExtras());

        intent.putExtra("passengerNameInEnglish",passengerNameInEnglish);
        intent.putExtra("passengerNameInHebrew",passengerNameInHebrew);
        intent.putExtra("passengerPhoneNumber",passengerPhoneNumber);
        intent.putExtra("passengerAddress",passengerAddress);
//        intent.putExtra("administratorPermission",adminPermission);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    protected void createPassengersList(String jsonResponse){
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("passengers");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String passengerNameInEnglish = jo.getString("nameInEnglish");
                String passengerNameInHebrew = jo.getString("nameInHebrew");
                String passengerPhoneNumber = jo.getString("phoneNumber");
                String passengerAddress = jo.getString("address");

                HashMap<String, String> passenger = new HashMap<>();
                passenger.put("passengerNameInEnglish", passengerNameInEnglish);
                passenger.put("passengerNameInHebrew", passengerNameInHebrew);
                passenger.put("passengerPhoneNumber", "0" + passengerPhoneNumber);
                passenger.put("passengerAddress", passengerAddress);

                passengerList.add(passenger);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

