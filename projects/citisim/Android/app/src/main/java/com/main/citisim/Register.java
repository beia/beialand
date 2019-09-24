package com.main.citisim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = (Button) findViewById(R.id.registerButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstName = (EditText) findViewById(R.id.editText_firstName);
                EditText lastName = (EditText) findViewById(R.id.editText_lastName);
                EditText email = (EditText) findViewById(R.id.editText_email);
                EditText password = (EditText) findViewById(R.id.editText_password);
                EditText repeatPassword = (EditText) findViewById(R.id.editText_repeatPassword);

                if(!password.getText().toString().equals( repeatPassword.getText().toString())){
                    ///TODO: pop-up message "Password not ok"
                    Toast.makeText(getApplicationContext(),"Password not ok",Toast.LENGTH_SHORT).show();
                }else {


                    HashMap data = new HashMap();
                    data.put("email", email.getText().toString());
                    data.put("firstName", firstName.getText().toString());
                    data.put("lastName", lastName.getText().toString());
                    data.put("password", password.getText().toString());


                    String url = getResources().getString(R.string.api_server) + "/api/register";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                    openMain();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(getApplicationContext(), "wronguser/pass", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"???", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    }
                }

        });
    }

    public void openMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
