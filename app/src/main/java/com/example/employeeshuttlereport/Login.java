package com.example.employeeshuttlereport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextView t;
    HashMap<String, String> user = new HashMap<>();
    String language = Locale.getDefault().getDisplayLanguage();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        t = (TextView) findViewById(R.id.logiginInEditText);

        validation();

    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void validation() {

        final ProgressDialog loading;
        if(language.equalsIgnoreCase("English")) {
            loading  = ProgressDialog.show(this, "Validate user", "Please wait");
        }
        else {
            loading = ProgressDialog.show(this,"מאמת את פרטי המשתמש","אנא המתן");
        }
        final String phone = getIntent().getSerializableExtra("username").toString().trim();
        final String pass = getIntent().getSerializableExtra("password").toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbynBJENIsnTs71omq_r6iUDksGmOxjgLwRIxO_eQpwQ9hPT6PRo/exec?action=validateUser",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        parseUser(response);
                        Toast.makeText(Login.this, user.get("answer"), Toast.LENGTH_LONG).show();
                        chooseNextIntent(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action", "validateUser");
                params.put("phoneNumber", phone);
                params.put("password", pass);


                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }

    private void parseUser(String jsonResponse) {

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("user");
            JSONObject jo = jarray.getJSONObject(0);

            String answer = jo.getString("answer");

            user.put("answer", answer);

            if(answer.equalsIgnoreCase("admin successfully logged in") || answer.equalsIgnoreCase("successfully logged in")) {
                String nameInEnglish = jo.getString("nameInEnglish");
                String nameInHebrew = jo.getString("nameInHebrew");
                String phoneNumber = jo.getString("phoneNumber");
                String administratorPermission = jo.getString("administratorPermission");

                user.put("nameInEnglish", nameInEnglish);
                user.put("nameInHebrew", nameInHebrew);
                user.put("phoneNumber", "0" + phoneNumber);
                user.put("administratorPermission", administratorPermission);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void chooseNextIntent(String response)
    {
        Bundle bundle = new Bundle();

        String answer = user.get("answer");

        if(answer.equalsIgnoreCase("admin successfully logged in") || answer.equalsIgnoreCase("successfully logged in"))
        {
            bundle.putString("administratorPermission",user.get("administratorPermission"));
            bundle.putString("phoneNumber",user.get("phoneNumber"));
            bundle.putString("nameInHebrew",user.get("nameInHebrew"));
            bundle.putString("nameInEnglish",user.get("nameInEnglish"));

            Intent intent = new Intent(getApplicationContext(), AdminMenu.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}


