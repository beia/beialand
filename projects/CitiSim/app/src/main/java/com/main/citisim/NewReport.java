package com.main.citisim;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NewReport extends AppCompatActivity {

    //vars
    private Button backToReports;
    private Button addImage;
    private Button submitImage;
    ImageView image;
    private static final int PICK_IMAGE=100;
    Uri imageUri;
    EditText latitude;
    EditText longitude;
    EditText title;
    final HashMap info = new HashMap();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        getUserInfo();

        latitude=(EditText)findViewById(R.id.latitudeView);
        longitude=(EditText)findViewById(R.id.longitudeView);
        title=(EditText)findViewById(R.id.title);



        image=(ImageView)findViewById(R.id.image) ;
        backToReports=(Button)findViewById(R.id.backToReports);
        backToReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap2();
            }
        });

        addImage=(Button)findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        submitImage=(Button)findViewById(R.id. submitImage);
        submitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            latitude.setText(Double.toString(Map.latitude));
            longitude.setText(Double.toString(Map.longitude));







                HashMap data = new HashMap();
               data.put("latitude",Double.toString(Map.latitude));
                data.put("longitude", Double.toString(Map.longitude));
                data.put("altitude","0");
                data.put("userId", info.get("id").toString());
               // data.put("photoDescription","0");
                //data.put("photoPath","0");

                //data.put("status","REPORT_CREATED");
                data.put("title",title.getText().toString());
                //data.put("id","0");
                data.put("date","0");
                data.put("file","0");



                String url = getResources().getString(R.string.api_server)+"/api/reports";

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
                                    Toast.makeText(getApplicationContext(),error.toString()+"aici",Toast.LENGTH_SHORT).show();
                                    Log.d("eroare", error.toString());

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

                ////////////////////////////////////
            }
        });


    }


    public void getUserInfo(){
        final String url = getResources().getString(R.string.api_server) + "/api/account";




        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //firstName.setText(""+response.toString());
                        try {

                           info.put("id", response.getString("id"));
                            Toast.makeText(NewReport.this, info.get("id").toString(), Toast.LENGTH_SHORT).show();

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







    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }



    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            }





        }





    public void openMap2(){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }



    public static String get64BaseImage (Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }



    public void sendImage(){

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("...");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

// This attaches the file to the POST:
        File f = new File(imageUri.getPath());
        try {
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(f),
                    ContentType.APPLICATION_OCTET_STREAM,
                    f.getName()
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(uploadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity responseEntity = response.getEntity();

    }


}
