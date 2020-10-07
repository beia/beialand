package com.main.citisim;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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

import static com.main.citisim.MapActivity.context;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static  String token;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    boolean shown;
    boolean isReset=false;
    public static String url;
    public static Context context;
    public static boolean rememberPassword = false;
    //init UI
    public static ScrollView loginScrollView;
    public static EditText username;
    public static EditText password;
    public static TextView okmsg;
    public static TextView usernametext;
    public static TextView passwordtext;
    public static Button addBtn;
    public static Button register;
    public static Button guest;
    public static ImageButton showPass;



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

        //init data
        url = getResources().getString(R.string.api_server) + "/api/authenticate";
        context = getApplicationContext();

        //check if the user logged in before
        String usernameString, passwordString;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        usernameString = sharedPref.getString("username", null);
        passwordString = sharedPref.getString("password", null);
        if(usernameString != null && passwordString != null)
        {
            //make the screen white
            loginScrollView = findViewById(R.id.loginScrollView);
            loginScrollView.setVisibility(View.GONE);
            //automatic login
            HashMap userData = new HashMap<>();
            userData.put("username", usernameString);
            userData.put("password", passwordString);
            userData.put("rememberMe", true);
            automaticLogin(userData);
        }

        //init UI
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        okmsg = (TextView) findViewById(R.id.okmsg);
        usernametext = (TextView)findViewById(R.id.Username);
        passwordtext = (TextView)findViewById(R.id.Password);
        addBtn = (Button) findViewById(R.id.addDevice);
        register= ( Button) findViewById(R.id.register);
        guest= ( Button) findViewById(R.id.guest);
        showPass = (ImageButton) findViewById(R.id.showPass);

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


                if (isReset == false)
                {
                    HashMap userData = new HashMap();
                    userData.put("username", username.getText().toString());
                    userData.put("password", password.getText().toString());
                    userData.put("rememberMe", true);
                    login(userData);
                }
                else
                {
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
                  E                      Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

    public static void openMap()
    {
        Intent intent = new Intent(context, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    public static void login(final HashMap<String, String> userData)
    {
        //------LOGARE-------
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(userData), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                okmsg.setText("Authentication succesful");
                try
                {
                    // Procesare continut raspuns
                    token = response.getString("id_token");//throws exception if the username or password are not valid
                    Log.d("LOGIN", "onResponse: " + token);
                    Session session = new Session(context);
                    session.setAuthToken(token);
                    //save the username and password for automatic login
                    if(rememberPassword) {
                        editor = sharedPref.edit();
                        editor.putString("username", userData.get("username"));
                        editor.putString("password", userData.get("password"));
                        editor.commit();
                    }
                    // Procesare raspuns
                    openMap();
                }
                catch (JSONException e)
                {
                    Toast.makeText(context,"Invalid response from server.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "wrong user/pass", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "????", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Access the RequestQueue through your singleton class.
        MyVolleyQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
        //------LOGARE-------//
    }


    public static void automaticLogin(HashMap<String, String> userData)
    {
        //------LOGARE AUTOMATA-------
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(userData), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                okmsg.setText("Authentication succesful");
                try
                {
                    // Procesare continut raspuns
                    token = response.getString("id_token");//throws exception if the username or password are not valid
                    Log.d("LOGIN", "onResponse: " + token);
                    Session session = new Session(context);
                    session.setAuthToken(token);
                    // Procesare raspuns
                    openMap();
                }
                catch (JSONException e)
                {
                    Toast.makeText(context,"Invalid response from server.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "wrong user/pass", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "????", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Access the RequestQueue through your singleton class.
        MyVolleyQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
        //------LOGARE AUTOMATA-------//
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.rememberMeCheckbox:
                if (checked)
                {
                    rememberPassword = true;
                }
                else
                {
                    rememberPassword = false;
                }
                break;
            default:
                break;
        }
    }

}

