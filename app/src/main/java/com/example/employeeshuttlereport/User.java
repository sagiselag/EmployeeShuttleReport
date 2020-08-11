package com.example.employeeshuttlereport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class User extends AppCompatActivity implements View.OnClickListener {
    TextView name_tv, hebName_tv, phone_tv, pass_tv;
    EditText name_et, hebName_et, phone_et, pass_et;
    Button buttonAddUser, buttonDeleteUser;
    Switch adminSwitch;
    String name, hebName, phone, pass, userAdministratorPermission, previousPhoneNumber;
    boolean update = false;
    String url = "https://script.google.com/macros/s/AKfycbynBJENIsnTs71omq_r6iUDksGmOxjgLwRIxO_eQpwQ9hPT6PRo/exec";
    String language = Locale.getDefault().getDisplayLanguage();
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_user);

        bundle = new Bundle(getIntent().getExtras());
        Intent intent = getIntent();
        name = intent.getStringExtra("userNameInEnglish");
        hebName = intent.getStringExtra("userNameInHebrew");
        phone = intent.getStringExtra("userPhoneNumber");
        previousPhoneNumber = intent.getStringExtra("userPhoneNumber");
        pass = intent.getStringExtra("password");
        userAdministratorPermission = intent.getStringExtra("userAdministratorPermission");

        name_tv = (TextView) findViewById(R.id.add_user_name_tv);
        hebName_tv = (TextView)findViewById(R.id.add_user_heb_name_tv);
        phone_tv = (TextView)findViewById(R.id.add_user_phone_tv);
        pass_tv = (TextView)findViewById(R.id.add_user_pass_tv);

        name_et = (EditText)findViewById(R.id.adduser_name_et);
        hebName_et = (EditText)findViewById(R.id.adduser_heb_name_et);
        phone_et = (EditText)findViewById(R.id.adduser_phone_et);
        pass_et = (EditText)findViewById(R.id.adduser_pass_et);

        adminSwitch = (Switch)findViewById(R.id.admin_switch);

        buttonAddUser = (Button)findViewById(R.id.btn_addUser);
        buttonAddUser.setOnClickListener(this);

        buttonDeleteUser = (Button)findViewById(R.id.btn_deleteUser);
        buttonDeleteUser.setOnClickListener(this);

        if(name != null) {
            name_et.setText(name);
            hebName_et.setText(hebName);
            phone_et.setText(phone);
            pass_et.setText(pass);

            if (userAdministratorPermission.equalsIgnoreCase("Yes") || userAdministratorPermission.equalsIgnoreCase("כן")) {
                adminSwitch.setChecked(true);
            } else {
                adminSwitch.setChecked(false);
            }

            if(language.equalsIgnoreCase("English")) {
                buttonAddUser.setText("Update");
            }
            else{
                buttonAddUser.setText("עדכן");
            }

            update = true;

            buttonDeleteUser.setClickable(true);
            buttonDeleteUser.setVisibility(View.VISIBLE);
        }


    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addUserToSheet() {

        final ProgressDialog loading;
        if(language.equalsIgnoreCase("English")) {
            loading = ProgressDialog.show(this,"Adding user","Please wait");
        }
        else {
            loading = ProgressDialog.show(this,"מוסיף את המשתמש","אנא המתן");
        }
        final String name = name_et.getText().toString().trim();
        final String hebName = hebName_et.getText().toString().trim();
        final String phone = phone_et.getText().toString().trim();
        final String password = pass_et.getText().toString().trim();
        final String userAdministratorPermission;

        if(adminSwitch.isChecked()) {
            userAdministratorPermission = adminSwitch.getTextOn().toString().trim();
        }
        else{
            userAdministratorPermission = adminSwitch.getTextOff().toString().trim();
        }



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(User.this,response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
                        bundle = new Bundle(getIntent().getExtras());
                        bundle.remove("userNameInEnglish");
                        bundle.remove("userNameInHebrew");
                        bundle.remove("userPhoneNumber");
                        bundle.remove("userAdministratorPermission");
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
                    parmas.put("action", "addUser");
                }
                else{
                    parmas.put("action", "updateUser");
                    parmas.put("previousPhoneNumber", previousPhoneNumber);
                }
                parmas.put("nameInEnglish",name);
                parmas.put("nameInHebrew",hebName);
                parmas.put("phoneNumber",phone);
                parmas.put("password",password);
                parmas.put("administratorPermission",userAdministratorPermission);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    private void deleteUserFromSheet(final String i_phone) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(User.this,response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
                        bundle = new Bundle(getIntent().getExtras());
                        bundle.remove("userNameInEnglish");
                        bundle.remove("userNameInHebrew");
                        bundle.remove("userPhoneNumber");
                        bundle.remove("userAdministratorPermission");
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
                parmas.put("action", "deleteUser");
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

        if(v==buttonAddUser){
            bundle.remove("userNameInEnglish");
            bundle.remove("userNameInHebrew");
            bundle.remove("userPhoneNumber");
            bundle.remove("userAdministratorPermission");
            addUserToSheet();
        }

        if(v==buttonDeleteUser){
            deleteUserFromSheet(previousPhoneNumber);
        }
    }
}


