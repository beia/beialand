package com.main.citisim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        final EditText oldpass = (EditText) findViewById(R.id.oldPass);
        final EditText newPass1 = (EditText) findViewById(R.id.newPass1);
        final EditText newPass2 = (EditText) findViewById(R.id.newPass2);

        Button confirm = (Button)findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass1.getText().toString().equals(newPass2.getText().toString())) {
                    ///////////
                    HashMap data = new HashMap();
                    data.put("currentPassword", oldpass.getText().toString());
                    data.put("newPassword", newPass1.getText().toString());

                    String url = getResources().getString(R.string.api_server)+"/api/account/change-password";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    if(error instanceof TimeoutError || error instanceof NoConnectionError){
                                        Toast.makeText(getApplicationContext(),"no internet connection",Toast.LENGTH_SHORT).show();
                                    }else if(error instanceof AuthFailureError){
                                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                    ) {
                        @Override
                        public java.util.Map<String, String> getHeaders() {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("Authorization", "Bearer " + new Session(getApplication()).getAuthToken());
                            return params;
                        }
                    };

                    // Access the RequestQueue through your singleton class.
                    MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    ///////////
                } else {
                    Toast.makeText(getApplicationContext(), "The password and the confirmation should match.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
