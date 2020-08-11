package com.example.employeeshuttlereport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PassengerForShuttle extends PassengerManagement {

    ArrayList<String> shuttlePassengers;
    String shuttleNumber = "New", driverNameInHebrew, driverPhoneNumber, passengerPhoneNumber, passengerNameInHebrew, passengerNameInEnglish;
    String waitingTime = "";
    String language = Locale.getDefault().getDisplayLanguage();


    @Override
    protected void initialize(){

        Intent intent = getIntent();
        Bundle bundle = new Bundle(getIntent().getExtras());

        if (intent.hasExtra("shuttlePassengers"))
        {
            shuttlePassengers = intent.getStringArrayListExtra("shuttlePassengers");
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

        if (intent.hasExtra("passengerPhoneNumber"))
        {
            passengerPhoneNumber = intent.getStringExtra("passengerPhoneNumber");
        }

        if (intent.hasExtra("passengerNameInHebrew"))
        {
            passengerNameInHebrew = intent.getStringExtra("passengerNameInHebrew");
        }

        if (intent.hasExtra("passengerNameInEnglish"))
        {
            passengerNameInEnglish = intent.getStringExtra("passengerNameInEnglish");
        }

        passengerListView = (ListView) findViewById(R.id.lv_passengers);
        passengerListView.setOnItemClickListener(this);

        buttonAddPassenger = (Button)findViewById(R.id.passengerManagement_addPassenger_button);


        if(language.equalsIgnoreCase("English")) {
            buttonAddPassenger.setText("Add Passengers to shuttle");
        }
        else{
            buttonAddPassenger.setText("הוסף נוסעים לנסיעה הנוכחית");
        }
        buttonAddPassenger.setOnClickListener(this);

        shuttlePassengers = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {

        if (v == buttonAddPassenger) {
            if(shuttlePassengers.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), Shuttle.class);
                Bundle bundle = new Bundle(getIntent().getExtras());

                bundle.putStringArrayList("shuttlePassengers", shuttlePassengers);
                bundle.putString("administratorPermission", adminPermission);
                bundle.putString("waitingTime", waitingTime);
                bundle.putString("shuttleNumber", shuttleNumber);
                bundle.putString("driverNameInHebrew", driverNameInHebrew);
                bundle.putString("driverPhoneNumber", driverPhoneNumber);
                bundle.putString("passengerPhoneNumber", passengerPhoneNumber);
                bundle.putString("passengerNameInHebrew", passengerNameInHebrew);
                bundle.putString("passengerNameInEnglish", passengerNameInEnglish);

                intent.putExtras(bundle);

                startActivity(intent);
            }
            else {
                if(language.equalsIgnoreCase("English")) {
                    Toast.makeText(getApplicationContext(), "Minimum 1 passenger.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "מינימום נוסע אחד", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void parsePassengers(String jsonResponse) {

        passengerList = new ArrayList<>();

        createPassengersList(jsonResponse);
        adapter = new SimpleAdapter(this,passengerList,R.layout.passenger_row_for_shuttle,
                new String[]{"","passengerNameInHebrew","checkBox"},new int[]{R.id.passengerNameInEnglish, R.id.passengerNameInHebrew, R.id.passengerCheckBox});

        if(language.equalsIgnoreCase("English")){
            adapter = new SimpleAdapter(this,passengerList,R.layout.passenger_row_for_shuttle,
                    new String[]{"passengerNameInEnglish","","checkBox"},new int[]{R.id.passengerNameInEnglish, R.id.passengerNameInHebrew, R.id.passengerCheckBox});
        }


        passengerListView.setAdapter(adapter);
        loading.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.passengerCheckBox);
            HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
            String passengerNameInHebrew = map.get("passengerNameInHebrew").toString();
            String passengerNameInEnglish = map.get("passengerNameInEnglish").toString();

            if(!checkBox.isChecked()){
                if(shuttlePassengers.size() < 4){
                    checkBox.setChecked(true);
                    if(language.equalsIgnoreCase("English")){
                        shuttlePassengers.add(passengerNameInEnglish);
                    }
                    else {
                        shuttlePassengers.add(passengerNameInHebrew);
                    }
                }
                else{
                    if(language.equalsIgnoreCase("English")) {
                        Toast.makeText(getApplicationContext(), "Maximum 4 passengers.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "מקסימום 4 נוסעים", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else{
                checkBox.setChecked(false);
                shuttlePassengers.remove(passengerNameInHebrew);
            }
        }
    }
}
