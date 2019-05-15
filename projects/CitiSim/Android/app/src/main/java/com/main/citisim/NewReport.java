package com.main.citisim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
/*
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewReport extends AppCompatActivity {

    //vars
    private Button backToReports;
    private ImageButton addImage;
    private Button submitImage;
    ImageView image;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Bitmap image_bitmap;
    EditText latitude;
    EditText longitude;
    EditText title;
    final HashMap info = new HashMap();

    private Uri buildURI(String url, Map<String, String> params) {

        // build url with parameters.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private void uploadImage(final Bitmap bitmap, final String url, Map<String, String> query_params) {

        String full_url = buildURI(url, query_params).toString();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, full_url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Log.d("raspuns", response.data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed to submit report: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            Log.d("error_response", jsonError);
                        }
                    }
                }) {

            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + new Session(getApplication()).getAuthToken());
                return params;
            }

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        getUserInfo();

        title = (EditText) findViewById(R.id.title);


        image = (ImageView) findViewById(R.id.image);
        //
        image.setVisibility(View.INVISIBLE);
        backToReports = (Button) findViewById(R.id.backToReports);
        backToReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap2();
            }
        });

        addImage = (ImageButton) findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        submitImage = (Button) findViewById(R.id.submitImage);
        submitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            /*latitude.setText(Double.toString(MapActivity.latitude));
            longitude.setText(Double.toString(MapActivity.longitude));
*/


                HashMap query_params = new HashMap();
                query_params.put("latitude", Double.toString(MapActivity.latitude));
                query_params.put("longitude", Double.toString(MapActivity.longitude));
                query_params.put("altitude", "0");
                query_params.put("userId", info.get("id").toString());
                // data.put("photoDescription","0");
                //data.put("photoPath","0");

                //data.put("status","REPORT_CREATED");
                query_params.put("title", title.getText().toString());
                //data.put("id","0");
                query_params.put("date", "0");


                String url = getResources().getString(R.string.api_server) + "/api/reports";

                submitImage.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Submitting report...", Toast.LENGTH_LONG).show();
                uploadImage(image_bitmap, url, query_params);


            }
        });


    }


    public void getUserInfo() {
        final String url = getResources().getString(R.string.api_server) + "/api/account";


        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //firstName.setText(""+response.toString());
                        try {

                            info.put("id", response.getString("id"));
                            // Toast.makeText(NewReport.this, info.get("id").toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", "??");
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

        // add it to the RequestQueue
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            image.setVisibility(View.VISIBLE);

            Log.d("hau", "am ajuns aici");

            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita
            //aici imaginea trebuie rotita

            image_bitmap = null;
            try {
                image_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    public void openMap2() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


}
