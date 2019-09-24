package com.main.citisim;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static  String token;
    boolean shown;
    boolean isReset=false;


    private void openRegisterPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.api_server) + "/#/register")));
    }

    private void openResetPasswordPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.api_server) + "/#/reset/request")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText username = (EditText) findViewById(R.id.username);
        final  EditText password = (EditText) findViewById(R.id.password);
        final TextView okmsg = (TextView) findViewById(R.id.okmsg);
        final TextView usernametext = (TextView)findViewById(R.id.Username);
        final TextView passwordtext = (TextView)findViewById(R.id.Password);
        final Button addBtn = (Button) findViewById(R.id.addDevice);
        final Button register= ( Button) findViewById(R.id.register);
        final Button guest= ( Button) findViewById(R.id.guest);
        final ImageButton showPass = (ImageButton) findViewById(R.id.showPass);

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               openRegisterPage();
           }
       });

       showPass.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(shown==true) {
                   password.setTransformationMethod(new PasswordTransformationMethod());
                   shown = false;
               }else{password.setTransformationMethod(null);
                   shown=true;}
           }
       });


       okmsg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               openResetPasswordPage();

             /*  if(isReset==false) {
                   usernametext.setText("email");
                   passwordtext.setText("");
                   //username.setVisibility(View.GONE);
                   password.setVisibility(View.GONE);
                   register.setVisibility(View.GONE);
                   addBtn.setText("Reset password");
                   showPass.setVisibility(View.GONE);
                   guest.setVisibility(View.GONE);
                   isReset = true;
                   okmsg.setText("Remember password?");
               }else {

                   usernametext.setText("Username");
                   passwordtext.setText("Password");
                   username.setText("");
                   //username.setVisibility(View.GONE);
                   password.setVisibility(View.VISIBLE);
                   register.setVisibility(View.VISIBLE);
                   addBtn.setText("LOG IN");
                   showPass.setVisibility(View.VISIBLE);
                   guest.setVisibility(View.VISIBLE);
                   isReset = false;
                   okmsg.setText("Forgot password?");

               }
        */
           }
       });



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isReset == false) {
                    //------LOGARE-------

                    HashMap data = new HashMap();
                    data.put("username", username.getText().toString());
                    data.put("password", password.getText().toString());
                    data.put("rememberMe", true);

                    String url = getResources().getString(R.string.api_server) + "/api/authenticate";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    okmsg.setText("Authentication succesful");
                                    try {
                                        // Procesare continut raspuns
                                        token = response.getString("id_token");
                                        Session session = new Session(getApplicationContext());
                                        session.setAuthToken(token);
                                        // Procesare raspuns
                                        openMap();
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), "Invalid response from server.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(getApplicationContext(), "wrong user/pass", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

// Access the RequestQueue through your singleton class.
                    MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


                    //------LOGARE-------//
                }else{


                    HashMap data = new HashMap();
                    data.put("mail", username.getText().toString());

                    Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_LONG).show();

                   // String url = getResources().getString(R.string.api_server) + "/api/account/reset-password/init";
                    String url = "http://citisim.taiger.com:8080/api/account/reset-password/init";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(getApplicationContext(), "wrong user/pass", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("wrongKey",error.toString());
                                    }
                                }
                            });

// Access the RequestQueue through your singleton class.
                    MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

                }
            }
        });
    }

    public void openRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void openMap(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG,"isServicesOK: a version error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this,"an error occured",Toast.LENGTH_SHORT).show();
        }
        return false;
    }





}

