package com.example.employeeshuttlereport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Passenger extends AppCompatActivity implements View.OnClickListener {
    TextView name_tv, hebName_tv, phone_tv, address_tv;
    EditText name_et, hebName_et, phone_et, address_et;
    Button buttonAddPassenger, buttonDeletePassenger;
    String name, hebName, phone, address, adminPermission, previousPhoneNumber;
    boolean update = false;
    String url = "https://script.google.com/macros/s/AKfycbyImjCwgc9ZeJL4Al979ItmwlgFdNSyrLnwu2NlCIPvBDdoKl8/exec";

    String language = Locale.getDefault().getDisplayLanguage();
    Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger);

        bundle = new Bundle(getIntent().getExtras());

        Intent intent = getIntent();
        name = intent.getStringExtra("passengerNameInEnglish");
        hebName = intent.getStringExtra("passengerNameInHebrew");
        phone = intent.getStringExtra("passengerPhoneNumber");
        previousPhoneNumber = intent.getStringExtra("passengerPhoneNumber");
        address = intent.getStringExtra("passengerAddress");
        adminPermission = intent.getStringExtra("administratorPermission");

        name_tv = (TextView) findViewById(R.id.add_passenger_name_tv);
        hebName_tv = (TextView)findViewById(R.id.add_passenger_heb_name_tv);
        phone_tv = (TextView)findViewById(R.id.add_passenger_phone_tv);
        address_tv = (TextView)findViewById(R.id.add_passenger_address_tv);

        name_et = (EditText)findViewById(R.id.add_passenger_name_et);
        hebName_et = (EditText)findViewById(R.id.add_passenger_heb_name_et);
        phone_et = (EditText)findViewById(R.id.add_passenger_phone_et);
        address_et = (EditText)findViewById(R.id.add_passenger_address_et);

        buttonAddPassenger = (Button)findViewById(R.id.btn_addPassenger);
        buttonAddPassenger.setOnClickListener(this);

        buttonDeletePassenger = (Button)findViewById(R.id.btn_deletePassenger);
        buttonDeletePassenger.setOnClickListener(this);

        if(name != null) {
            name_et.setText(name);
            hebName_et.setText(hebName);
            phone_et.setText(phone);
            address_et.setText(address);

            if (adminPermission.equalsIgnoreCase("Yes") || adminPermission.equalsIgnoreCase("כן")) {
                buttonDeletePassenger.setClickable(true);
                buttonDeletePassenger.setVisibility(View.VISIBLE);
            }

            if(language.equalsIgnoreCase("English")) {
                buttonAddPassenger.setText("Update");
            }
            else{
                buttonAddPassenger.setText("עדכן");
            }

            update = true;
        }


    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addPassengerToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding passenger","Please wait");
        final String name = name_et.getText().toString().trim();
        final String hebName = hebName_et.getText().toString().trim();
        final String phone = phone_et.getText().toString().trim();
        final String address = address_et.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(Passenger.this,response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
                        intent.putExtra("administratorPermission",adminPermission);
                        bundle = new Bundle(getIntent().getExtras());
                        bundle.remove("passengerNameInEnglish");
                        bundle.remove("passengerNameInHebrew");
                        bundle.remove("passengerPhoneNumber");
                        bundle.remove("passengerAddress");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                if(!update) {
                    parmas.put("action", "addPassenger");
                }
                else{
                    parmas.put("action", "updatePassenger");
                    parmas.put("previousPhoneNumber", previousPhoneNumber);
                }
                parmas.put("nameInEnglish",name);
                parmas.put("nameInHebrew",hebName);
                parmas.put("phoneNumber",phone);
                parmas.put("address",address);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    private void deletePassengerFromSheet(final String i_phone) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(Passenger.this,response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
                        bundle = new Bundle(getIntent().getExtras());
                        bundle.remove("passengerNameInEnglish");
                        bundle.remove("passengerNameInHebrew");
                        bundle.remove("passengerPhoneNumber");
                        bundle.remove("passengerAddress");
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "deletePassenger");
                parmas.put("phoneNumber",i_phone);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }




    @Override
    public void onClick(View v) {

        if(v==buttonAddPassenger){
            addPassengerToSheet();
        }

        if(v==buttonDeletePassenger){
            deletePassengerFromSheet(previousPhoneNumber);
        }
    }
}


