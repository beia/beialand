package com.main.citisim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Settings extends AppCompatActivity {

     TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView firstName;
        final TextView lastName;

        Button changePass;
        Button deviceList;



        firstName=(TextView)findViewById(R.id.firstName);
        lastName=(TextView)findViewById(R.id.lastName);
        email=(TextView)findViewById(R.id.email);
        changePass=(Button)findViewById(R.id.changePass);
        deviceList=(Button)findViewById(R.id.devicesList);

        deviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeviceList();
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            openChangePass();



            }
        });


        /////////////////GETDATAFROMVOLLEY/////////////////

        getUserInfo();

        /////////////////GETDATAFROMVOLLEY/////////////////

    }

    public void getUserInfo(){
        final String url = getResources().getString(R.string.api_server) + "/api/account";


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //firstName.setText(""+response.toString());
                        try {
                            email.setText(response.getString("email"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response","??" );
                    }
                }
        ){
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + new Session(getApplication()).getAuthToken());
                return params;
            }
        };

// add it to the RequestQueue
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }


    public void openChangePass(){
        Intent intent = new Intent(this, ChangePass.class);
        startActivity(intent);
    }

    public void openDeviceList(){
        Intent intent = new Intent(this, DevicesList.class);
        startActivity(intent);
    }

}
