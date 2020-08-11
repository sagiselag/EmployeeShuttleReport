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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShuttleManagement extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    ListView shuttleListView;
    ListAdapter adapter;
    ProgressDialog loading;
    ArrayList<HashMap<String, String>> shuttleList;
    String getUrl = "https://script.google.com/macros/s/AKfycbxQJiDFa0yrBltY6Hy92JhGZvVx2kCaAJmgY0Ca2EJlmMFmsfBd/exec?action=getShuttles";
    String adminPermission, phoneNumber, nameInHebrew, nameInEnglish;
    Boolean forDriverOnly = false;
    double totalPerTimePeriod = 0;
    TextView totalAmount;
    String language = Locale.getDefault().getDisplayLanguage();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_shuttles);

        Intent intent = getIntent();
        adminPermission = intent.getStringExtra("administratorPermission");
        phoneNumber = intent.getStringExtra("phoneNumber");
        if(intent.hasExtra("forDriverOnly")){
            forDriverOnly = Boolean.valueOf(intent.getStringExtra("forDriverOnly"));
        }

        initialize();

        getShuttles();

        totalAmount = findViewById(R.id.shuttleListAmount_tv);
        totalAmount.setText(String.format("%.2f", totalPerTimePeriod));

    }

    protected void initialize(){
        shuttleListView = (ListView) findViewById(R.id.lv_shuttles);
        shuttleListView.setOnItemClickListener(this);

    }

    protected void getShuttles() {

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
                        parseShuttles(response);
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


    protected void parseShuttles(String jsonResponse) {

        shuttleList = new ArrayList<>();


        createShuttlesList(jsonResponse, forDriverOnly);

        adapter = new SimpleAdapter(this,shuttleList,R.layout.list_shuttle_row,
                new String[]{"shuttleNumber","dateOfShuttle","driverNameInHebrew","passengersNames","shuttlePriceIncludeTax"},new int[]{R.id.shuttleNumber_tv, R.id.shuttleDate_tv, R.id.shuttleDriverNameInHeb_tv , R.id.shuttlePassengersNames_tv, R.id.shuttlePriceIncludeTax_tv});


        shuttleListView.setAdapter(adapter);
        loading.dismiss();
    }

    @Override
    public void onClick(View v) {
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Shuttle.class);
        Bundle bundle = new Bundle(getIntent().getExtras());

        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String shuttleNumber = map.get("shuttleNumber").toString();
        String dateOfShuttle = map.get("dateOfShuttle").toString();
        String passengersNames = map.get("passengersNames").toString();
        String driverPhoneNumber = map.get("driverPhoneNumber").toString();

        HashMap<String, String> shuttle = findShuttle(shuttleNumber);
        String waitingTimeAddition = shuttle.get("waitingTimeAddition").toString();
        String driverNameInHebrew = shuttle.get("driverNameInHebrew").toString();

        int waitingTimeInt = 0;
        String waitingTime = "";


        String[] passengersNamesArray = passengersNames.split(",");
        ArrayList<String> passengers = new ArrayList<>();

        for (String name: passengersNamesArray) {
            passengers.add(name);
        }

        if(waitingTimeAddition.length() > 0) {
            waitingTimeInt = Integer.parseInt(waitingTimeAddition) / Shuttle.minuteWaitingCost;
        }

        if(waitingTimeInt >= 0){
            waitingTime = String.valueOf(waitingTimeInt);
        }



        bundle.putString("shuttleNumber",shuttleNumber);
        bundle.putString("dateOfShuttle",dateOfShuttle);

        bundle.putStringArrayList("shuttlePassengers", passengers);
        bundle.putString("administratorPermission",adminPermission);
        bundle.putString("waitingTime",waitingTime);

        bundle.putString("driverNameInHebrew",driverNameInHebrew);

        bundle.putString("driverPhoneNumber",driverPhoneNumber);
        bundle.putString("userPhoneNumber",phoneNumber);
        bundle.putString("userNameInHebrew",nameInHebrew);
        bundle.putString("userNameInEnglish",nameInEnglish);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    protected void createShuttlesList(String jsonResponse, boolean forDriverOnly){
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("shuttles");

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String shuttleNumber = jo.getString("shuttleNumber");
                String dateOfShuttle = jo.getString("dateOfShuttle");
                String driverNameInHebrew = jo.getString("driverNameInHebrew");
                String driverPhoneNumber = "0" + jo.getString("driverPhoneNumber");

                String numberOfPassengers = jo.getString("numberOfPassengers");
                String extraPassengerAddition = jo.getString("extraPassengerAddition");
                String waitingTimeAddition = jo.getString("waitingTimeAddition");
                String shuttlePrice = jo.getString("shuttlePrice");
                String passengersNames = jo.getString("passengersNames");


                double shuttlePriceIncludeTaxdouble = Integer.parseInt(shuttlePrice) * 1.17;
                String shuttlePriceIncludeTax = String.format("%.2f", shuttlePriceIncludeTaxdouble);

                Date date =  Shuttle.dateParse(dateOfShuttle);
                dateOfShuttle = new SimpleDateFormat("dd/MM/yyyy   HH:mm").format(date);

                HashMap<String, String> shuttle = new HashMap<>();
                shuttle.put("shuttleNumber", shuttleNumber);
                shuttle.put("dateOfShuttle", dateOfShuttle);
                shuttle.put("driverNameInHebrew", driverNameInHebrew);
                shuttle.put("driverPhoneNumber", driverPhoneNumber);
                shuttle.put("numberOfPassengers", numberOfPassengers);
                shuttle.put("extraPassengerAddition", extraPassengerAddition);
                shuttle.put("waitingTimeAddition", waitingTimeAddition);
                shuttle.put("shuttlePrice", shuttlePrice);
                shuttle.put("shuttlePriceIncludeTax", shuttlePriceIncludeTax);
                shuttle.put("passengersNames", passengersNames);

                if(!forDriverOnly || (forDriverOnly && driverPhoneNumber.equalsIgnoreCase(phoneNumber)))
                {
                    shuttleList.add(shuttle);
                    totalPerTimePeriod += Double.valueOf(shuttlePriceIncludeTax);
                }

            }
            totalAmount.setText(String.format("%.2f", totalPerTimePeriod));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private HashMap<String, String> findShuttle(String shuttleNumber){
        for (HashMap<String, String> shuttle: shuttleList) {
            if(shuttle.get("shuttleNumber").equalsIgnoreCase(shuttleNumber)){
                return shuttle;
            }
        }
        return null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}


