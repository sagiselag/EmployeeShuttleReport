package com.example.employeeshuttlereport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class UserManagement extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    ListView listView;
    ListAdapter adapter;
    ProgressDialog loading;
    ArrayList<HashMap<String, String>> list;
    Button buttonAddUser;
    String getUrl = "https://script.google.com/macros/s/AKfycbynBJENIsnTs71omq_r6iUDksGmOxjgLwRIxO_eQpwQ9hPT6PRo/exec?action=getUsers";
    String language = Locale.getDefault().getDisplayLanguage();
    Bundle bundle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_user);

        bundle = new Bundle(getIntent().getExtras());
        listView = (ListView) findViewById(R.id.lv_users);
        listView.setOnItemClickListener(this);

        buttonAddUser = (Button)findViewById(R.id.userManagment_addUser_button);
        buttonAddUser.setOnClickListener(this);

        getUsers();

    }

    private void getUsers() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseUsers(response);
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


    private void parseUsers(String jsonResponse) {

        list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("users");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String userNameInEnglish = jo.getString("nameInEnglish");
                String userNameInHebrew = jo.getString("nameInHebrew");
                String userPhoneNumber = jo.getString("phoneNumber");
                String password = jo.getString("password");
                String administratorPermission = jo.getString("administratorPermission");



                HashMap<String, String> user = new HashMap<>();
                user.put("userNameInEnglish", userNameInEnglish);
                user.put("userNameInHebrew", userNameInHebrew);
                user.put("userPhoneNumber", "0" + userPhoneNumber);
                user.put("password", password);
                user.put("administratorPermission", administratorPermission);

                list.add(user);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this,list,R.layout.list_user_row,
                new String[]{"userNameInEnglish","userNameInHebrew","userPhoneNumber","password","administratorPermission"},new int[]{R.id.nameInEnglish, R.id.nameInHebrew, R.id.phoneNumber, R.id.password, R.id.administratorPermission});


        listView.setAdapter(adapter);
        loading.dismiss();
    }

    @Override
    public void onClick(View v) {

        if (v == buttonAddUser) {

            Intent intent = new Intent(getApplicationContext(), User.class);
            bundle = new Bundle(getIntent().getExtras());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, User.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String userNameInEnglish = map.get("userNameInEnglish").toString();
        String userNameInHebrew = map.get("userNameInHebrew").toString();
        String userPhoneNumber = map.get("userPhoneNumber").toString();
        String password = map.get("password").toString();
        String administratorPermission = map.get("administratorPermission").toString();


        // String sno = map.get("sno").toString();

        // Log.e("SNO test",sno);

        bundle = new Bundle(getIntent().getExtras());
        bundle.putString("password",password);
        bundle.putString("userAdministratorPermission",administratorPermission);
        bundle.putString("userNameInEnglish",userNameInEnglish);
        bundle.putString("userNameInHebrew",userNameInHebrew);
        bundle.putString("userPhoneNumber",userPhoneNumber);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
