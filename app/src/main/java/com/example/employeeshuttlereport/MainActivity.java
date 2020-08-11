package com.example.employeeshuttlereport;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable  {
    public static boolean res;
    Button buttonLogin;
    EditText password, username;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username =  (EditText)findViewById(R.id.editTextUsernameInput);
        password =  (EditText)findViewById(R.id.editTextPasswordInput);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == buttonLogin) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("password", password.getText().toString());

            startActivity(intent);
        }
    }

}
