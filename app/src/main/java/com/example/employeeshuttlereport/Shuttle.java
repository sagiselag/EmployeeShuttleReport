package com.example.employeeshuttlereport;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Shuttle extends AppCompatActivity implements View.OnClickListener{

    ArrayList<String> passengers = new ArrayList<>();
    static public int basicShuttlePrice = 87,extraPassengersCost = 25, minuteWaitingCost = 2;
    int shuttlePrice, extraPassengersAddition = 0, waitingTimeAddition = 0;
    StringBuilder passengersNames = new StringBuilder();
    String language = Locale.getDefault().getDisplayLanguage();

    String shuttleNumber = "New", driverNameInHebrew, driverPhoneNumber, phoneNumber, nameInHebrew, nameInEnglish, dateOfShuttle;

    boolean update = false;
    String url = "https://script.google.com/macros/s/AKfycbxQJiDFa0yrBltY6Hy92JhGZvVx2kCaAJmgY0Ca2EJlmMFmsfBd/exec";
    Button createNewShuttleButton, choosePassengersButton;

    TextView time_et, date_et;
    EditText waitingTime_et;

    Date date = new Date();
    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    String adminPermission = "No";

    String waitingTime = "0";

    TextView firstPassengerTV, secondPassengerTV, thirdPassengerTV, fourthPassengerTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle);
        Intent intent = getIntent();
        Bundle bundle = new Bundle(getIntent().getExtras());

        time_et =  (TextView)findViewById(R.id.editTextTime);
        date_et =  (TextView)findViewById(R.id.editTextDate);
        waitingTime_et  =  (EditText)findViewById(R.id.waitingTime_et);

        time_et.setText(currentTime);
        date_et.setText(currentDate);

        time_et.setOnClickListener(this);
        date_et.setOnClickListener(this);

        if (intent.hasExtra("shuttlePassengers"))
        {
            passengers = intent.getStringArrayListExtra("shuttlePassengers");
        }
        if (intent.hasExtra("administratorPermission"))
        {
            adminPermission = intent.getStringExtra("administratorPermission");
        }

        if (intent.hasExtra("waitingTime"))
        {
            waitingTime = intent.getStringExtra("waitingTime");
        }

        if (intent.hasExtra("shuttleNumber"))
        {
            shuttleNumber = intent.getStringExtra("shuttleNumber");
        }

        if (intent.hasExtra("driverNameInHebrew"))
        {
            driverNameInHebrew = intent.getStringExtra("driverNameInHebrew");
        }

        if (intent.hasExtra("driverPhoneNumber"))
        {
            driverPhoneNumber = intent.getStringExtra("driverPhoneNumber");
        }

        if (intent.hasExtra("phoneNumber"))
        {
            phoneNumber = intent.getStringExtra("phoneNumber");
        }

        if (intent.hasExtra("nameInHebrew"))
        {
            nameInHebrew = intent.getStringExtra("nameInHebrew");
        }

        if (intent.hasExtra("nameInEnglish"))
        {
            nameInEnglish = intent.getStringExtra("nameInEnglish");
        }

        if (intent.hasExtra("dateOfShuttle"))
        {
            dateOfShuttle = intent.getStringExtra("dateOfShuttle");
        }
        else{
            dateOfShuttle = date_et.getText() + " " + time_et.getText() ;
        }

        if(driverNameInHebrew == null){
            driverNameInHebrew = nameInHebrew;
            driverPhoneNumber = phoneNumber;
        }

        date = dateParse(dateOfShuttle);

        dateOfShuttle = date.toString();
        date_et.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        time_et.setText(new SimpleDateFormat("HH:mm").format(date));

        waitingTime_et.setText(waitingTime);

        initializePassengersTextViewText();

        choosePassengersButton = (Button)findViewById(R.id.select_shuttle_passengers_btn);
        choosePassengersButton.setOnClickListener(this);

        createNewShuttleButton = (Button)findViewById(R.id.create_shuttle_button);
        createNewShuttleButton.setOnClickListener(this);

        if(!shuttleNumber.equalsIgnoreCase("New")){
            if(driverPhoneNumber.equalsIgnoreCase(phoneNumber)){
                update = true;
                if(language.equalsIgnoreCase("English")) {
                    createNewShuttleButton.setText("Update");
                }
                else{
                    createNewShuttleButton.setText("עדכן");
                }
            }
            else{
                createNewShuttleButton.setClickable(false);
                createNewShuttleButton.setText("View only (other user shuttle)");
                date_et.setClickable(false);
                date_et.setKeyListener(null);
                time_et.setClickable(false);
                time_et.setKeyListener(null);
                waitingTime_et.setClickable(false);
                waitingTime_et.setKeyListener(null);
                choosePassengersButton.setClickable(false);
                choosePassengersButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void initializePassengersTextViewText() {
        firstPassengerTV = (TextView) findViewById(R.id.firstShuttlePassenger_tv);
        secondPassengerTV = (TextView) findViewById(R.id.secondShuttlePassenger_tv);
        thirdPassengerTV = (TextView) findViewById(R.id.thirdShuttlePassenger_tv);
        fourthPassengerTV = (TextView) findViewById(R.id.fourthShuttlePassenger_tv);
        TextView[] names = {firstPassengerTV, secondPassengerTV, thirdPassengerTV, fourthPassengerTV};

        if(passengers.size() > 0){
            for (int i = 0; i < passengers.size() ; i++) {
                names[i].setText(passengers.get(i));
            }
            for (int i = passengers.size() ; i < 4 ; i++){
                names[i].setText("");
            }
        }
        else{
            for (TextView name: names) {
                name.setText("");
            }
        }
    }


    @Override
    public void onClick(View v) {

        if (v == choosePassengersButton) {
            Intent intent = new Intent(getApplicationContext(), PassengerForShuttle.class);
            Bundle bundle = new Bundle(getIntent().getExtras());
            dateOfShuttle = date_et.getText() + "   " + time_et.getText() ;

            bundle.putStringArrayList("shuttlePassengers", passengers);
            bundle.putString("waitingTime",waitingTime);
            bundle.putString("shuttleNumber",shuttleNumber);
            bundle.putString("driverNameInHebrew",driverNameInHebrew);
            bundle.putString("driverPhoneNumber",driverPhoneNumber);
            bundle.putString("administratorPermission",adminPermission);
            bundle.putString("passengerPhoneNumber",phoneNumber);
            bundle.putString("passengerNameInHebrew",nameInHebrew);
            bundle.putString("passengerNameInEnglish",nameInEnglish);
            bundle.putString("dateOfShuttle",dateOfShuttle);
            bundle.putString("update", String.valueOf(update));

            intent.putExtras(bundle);

            startActivity(intent);
        }

        if (v == createNewShuttleButton) {
            dateOfShuttle = date_et.getText() + " " + time_et.getText() ;
            date = dateParse(dateOfShuttle);

            dateOfShuttle = date.toString();
            date_et.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
            time_et.setText(new SimpleDateFormat("HH:mm").format(date));

            if(passengers.size() > 0){
                addShuttleToSheet();
                Intent intent = new Intent(getApplicationContext(), AdminMenu.class);

                Bundle bundle = new Bundle(getIntent().getExtras());

                bundle.putString("administratorPermission",adminPermission);
                bundle.putString("phoneNumber",phoneNumber);
                bundle.putString("nameInHebrew",nameInHebrew);
                bundle.putString("nameInEnglish",nameInEnglish);
                bundle.remove("update");
                bundle.remove("shuttleNumber");

                intent.putExtras(bundle);

                startActivity(intent);
            }
            else{
                if(language.equalsIgnoreCase("English")) {
                    Toast.makeText(Shuttle.this, "In order to create new shuttle please add at least one passenger.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "על מנת ליצור נסיעה חדשה יש להוסיף לפחות נוסע אחד.", Toast.LENGTH_SHORT).show();
                }
            }


        }

        if (v == date_et) {
            DialogFragment newFragment = new DatePickerFragment(date_et);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }

        if (v == time_et) {
            DialogFragment newFragment = new TimePickerFragment(time_et);
            newFragment.show(getSupportFragmentManager(), "timePicker");
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addShuttleToSheet() {
        final ProgressDialog loading;
        if(language.equalsIgnoreCase("English")) {
            loading = ProgressDialog.show(this,"Adding shuttle","Please wait");
        }
        else {
            loading = ProgressDialog.show(this,"מוסיף את הנסיעה","אנא המתן");
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(Shuttle.this, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
                        Bundle bundle = getIntent().getExtras();

                        bundle.putString("administratorPermission",adminPermission);
                        bundle.putString("phoneNumber",phoneNumber);
                        bundle.putString("nameInHebrews",nameInHebrew);
                        bundle.putString("nameInEnglish",nameInEnglish);
                        bundle.remove("waitingTime");
                        bundle.remove("currentTime");
                        bundle.remove("currentDate");
                        bundle.remove("passengers");

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
                int waitingTime = Integer.parseInt(waitingTime_et.getText().toString());
                Map<String, String> params = new HashMap<>();
                int numberOfPassengers = passengers.size();
                extraPassengersAddition = (numberOfPassengers-2)*extraPassengersCost; // only two passengers in basic price


                int counter = 1;

                if (extraPassengersAddition < 0){
                    extraPassengersAddition = 0;
                }

                if (waitingTime - 10 >= 0){
                    waitingTimeAddition = waitingTime * minuteWaitingCost; // only 10 minutes waiting for free
                }
                else{
                    waitingTimeAddition = 0;
                }

                shuttlePrice = basicShuttlePrice + extraPassengersAddition + waitingTimeAddition;

                for (String passenger: passengers)
                {
                    passengersNames.append(passenger);
                    if(counter < numberOfPassengers){
                        passengersNames.append(", ");
                    }
                    counter++;
                }



                //here we pass params
                if(!update) {
                    params.put("action", "addShuttle");
                }
                else{
                    params.put("action", "updateShuttle");
                }
                params.put("shuttleNumber",shuttleNumber);
                params.put("dateOfShuttle",dateOfShuttle);
                params.put("driverNameInHebrew",driverNameInHebrew);
                params.put("driverPhoneNumber",driverPhoneNumber);
                params.put("numberOfPassengers", String.valueOf(numberOfPassengers));
                params.put("extraPassengerAddition", String.valueOf(extraPassengersAddition));
                params.put("waitingTimeAddition", String.valueOf(waitingTimeAddition));
                params.put("shuttlePrice", String.valueOf(shuttlePrice));

                params.put("passengersNames", passengersNames.toString());
                params.put("administratorPermission", adminPermission);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    public static Date dateParse(String dateStr){
        Date date =  new Date();
        boolean datePharseSucceed = false;
        while(!datePharseSucceed) {
            try {
                date = new SimpleDateFormat().parse(dateStr);
                datePharseSucceed = true;
            } catch (ParseException e) {
                e.printStackTrace();
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateStr);
                    datePharseSucceed = true;
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    try {
                        date = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss").parse(dateStr);
                        datePharseSucceed = true;
                    } catch (ParseException exc) {
                        exc.printStackTrace();
                        try {
                            date = new SimpleDateFormat("E MMM dd yyyy HH:mm").parse(dateStr);
                            datePharseSucceed = true;
                        } catch (ParseException exct) {
                            exct.printStackTrace();
                            dateStr = dateStr.substring(4,dateStr.length()-15);
                            try {
                                date = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss").parse(dateStr);
                                datePharseSucceed = true;
                            } catch (ParseException excte) {
                                excte.printStackTrace();
                                try {
                                    date = new SimpleDateFormat("EEE MMM dd yy HH:mm:ss").parse(dateStr);
                                    datePharseSucceed = true;
                                } catch (ParseException exctev) {
                                    exctev.printStackTrace();
                                    try {
                                        date = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss").parse(dateStr);
                                        datePharseSucceed = true;
                                    } catch (ParseException exctevd) {
                                        exctevd.printStackTrace();
                                        try {
                                            date = new SimpleDateFormat("EEE MMM dd YY HH:mm:ss").parse(dateStr);
                                            datePharseSucceed = true;
                                        } catch (ParseException exctevds) {
                                            exctevds.printStackTrace();
                                            try {
                                                date = new SimpleDateFormat().parse(dateStr);
                                                datePharseSucceed = true;
                                            } catch (ParseException exctevdsd) {
                                                exctevdsd.printStackTrace();
                                                try {
                                                    date = new SimpleDateFormat("MMM dd y HH:mm:ss", Locale.ENGLISH).parse(dateStr);
                                                    datePharseSucceed = true;
                                                } catch (ParseException exctess) {
                                                    exctess.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return date;
    }

}

//    Fri Oct 22 2004 15:10:00 GMT+0200 (IST)