package com.example.employeeshuttlereport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminMenu extends AppCompatActivity implements View.OnClickListener {

    TextView header;
    Button buttonListUser, buttonListPassengers, buttonNewShuttle, buttonListShuttles, buttonDriverListShuttles;
    String adminPermission, phoneNumber, nameInHebrew;
    Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_menu);

        Intent intent = getIntent();
        bundle = new Bundle(getIntent().getExtras());

        adminPermission = intent.getStringExtra("administratorPermission");
        phoneNumber = intent.getStringExtra("phoneNumber");
        nameInHebrew = intent.getStringExtra("nameInHebrew");

        bundle.putString("driverPhoneNumber", phoneNumber);
        bundle.putString("driverNameInHebrew", nameInHebrew);

        header = (TextView) findViewById(R.id.adminMenuTextView);

        buttonListUser = (Button)findViewById(R.id.button_list_users);
        buttonListUser.setOnClickListener(this);

        buttonListPassengers = (Button)findViewById(R.id.button_list_passengers);
        buttonListPassengers.setOnClickListener(this);

        buttonNewShuttle = (Button)findViewById(R.id.select_shuttle_passengers_btn);
        buttonNewShuttle.setOnClickListener(this);

        buttonListShuttles = (Button)findViewById(R.id.button_list_shuttles);
        buttonListShuttles.setOnClickListener(this);

        buttonDriverListShuttles = (Button)findViewById(R.id.button_driver_list_shuttles);
        buttonDriverListShuttles.setOnClickListener(this);

        if(adminPermission.equalsIgnoreCase("No")){
            header.setText("Menu");
            buttonListUser.setVisibility(View.INVISIBLE);
            buttonListUser.setClickable(false);
            buttonListShuttles.setVisibility(View.INVISIBLE);
            buttonListShuttles.setClickable(false);
        }

    }

    @Override
    public void onClick(View v) {
        bundle = new Bundle(getIntent().getExtras());

        if (v == buttonListUser) {

            Intent intent = new Intent(getApplicationContext(), UserManagement.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (v == buttonListPassengers) {

            Intent intent = new Intent(getApplicationContext(), PassengerManagement.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (v == buttonNewShuttle) {

            Intent intent = new Intent(getApplicationContext(), Shuttle.class);
            if(bundle.containsKey("shuttlePassengers")) bundle.remove("shuttlePassengers");
            if(bundle.containsKey("dateOfShuttle")) bundle.remove("dateOfShuttle");
            if(bundle.containsKey("update")) bundle.remove("update");
            if(bundle.containsKey("shuttleNumber")) bundle.remove("shuttleNumber");

            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (v == buttonListShuttles) {

            Intent intent = new Intent(getApplicationContext(), ShuttleManagement.class);
            bundle.putString("forDriverOnly", "false");
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (v == buttonDriverListShuttles) {

            Intent intent = new Intent(getApplicationContext(), ShuttleManagement.class);
            bundle.putString("forDriverOnly", "true");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}