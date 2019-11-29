package com.main.citisim;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class NewReport extends AppCompatActivity {

    //vars
    private Button backToReports;
    private ImageButton addImage;
    private ImageButton addImageCamera;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button submitImage;
    ImageView image;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Bitmap image_bitmap;
    EditText latitude;
    EditText longitude;
    EditText title;
    public static HashMap<String, String> info;
    public static HashMap<String, String> query_params;
    String category;
    Bitmap imageToBitmap;
    Bitmap imageBitmap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        //init variables
        info = new HashMap<>();
        query_params = new HashMap();
        Date c = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            c = Calendar.getInstance().getTime();
        }
        getUserInfo();

        final Spinner dropdown = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"CATEGORY ▼","STREET_LIGHTING", "REGULATED_PARKING", "TREES_AND_PARKS",
        "PUBLIC_BICYCLE","WALKWAYS_AND_SIDEWALKS","FOUNTAINS","CLEANING_IN_PUBLIC_SPACES",
        "FOUNTAINS","CLEANING_IN_PUBLIC_SPACES","PESTS","WITHDRAWAL_OF_ELEMENTS","SIGNS_AND_TRAFIC_LIGHTS",
        "ABANDONED_OR_WITHDRAWAL_VEHICLES","SANITARY_EMERGENCE","ROBBERY","FIGHT_OR_AGGRESSION",
        "TRAFFIC_ACCIDENT","GENRE_VIOLENCE","GUNFIGHT","ARCHITECTURAL_DAMAGE"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        ImageButton QRCodeReader = findViewById(R.id.QRCodeReader);
        QRCodeReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRActivity();
            }
        });

        title = (EditText) findViewById(R.id.reportTitle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        image = (ImageView) findViewById(R.id.image);
        //
        image.setVisibility(View.INVISIBLE);
        backToReports = (Button) findViewById(R.id.backToReports);
        backToReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewReports();
            }
        });

        addImage = (ImageButton) findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addImageCamera = findViewById(R.id.addImageCamera);
        addImageCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
               dispatchTakePictureIntent();
               galleryAddPic();
            }
        });


        submitImage = (Button) findViewById(R.id.submitImage);
        submitImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                String url = getResources().getString(R.string.api_server) + "/api/reports";
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

                query_params.put("latitude", Double.toString(MapActivity.latitude));
                query_params.put("longitude", Double.toString(MapActivity.longitude));
                query_params.put("altitude", "0");
                query_params.put("userId", info.get("id"));
                query_params.put("category",dropdown.getSelectedItem().toString());
                query_params.put("title", title.getText().toString());
                query_params.put("date", date);

                if(title.getText().length()<1)
                {
                    Toast.makeText(getApplicationContext(), "title is required", Toast.LENGTH_LONG).show();
                }
                else if(image.getVisibility()==View.GONE || image.getVisibility()==View.INVISIBLE)
                {
                    Toast.makeText(getApplicationContext(), "image is required", Toast.LENGTH_LONG).show();
                }
                else if(dropdown.getSelectedItem().toString().equals("CATEGORY ▼"))
                {
                    Toast.makeText(getApplicationContext(), "category is not valid", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Report submitted.", Toast.LENGTH_LONG).show();
                   // uploadImage(image_bitmap, url, query_params);
                    if(imageToBitmap!=null)
                    {
                        try
                        {
                            uploadImage(getCorrectBitmap(currentPhotoPath,imageToBitmap), url, query_params);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        uploadImage(image_bitmap, url, query_params);

                    submitImage.setEnabled(false);
                }
            }
        });
    }


    private Uri buildURI(String url, Map<String, String> params) {

        // build url with parameters.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
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
                        try
                        {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Log.d("raspuns", response.data.toString());
                        }
                        catch (JSONException e)
                        {
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







    public Bitmap getCorrectBitmap(String photoPath, Bitmap bitmap) throws IOException
    {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap = null;
        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        Bitmap bitmap2;
        if(rotatedBitmap.getWidth()>rotatedBitmap.getHeight()){
            float x=(float)rotatedBitmap.getHeight()/rotatedBitmap.getWidth()*600;
            bitmap2 = Bitmap.createScaledBitmap(rotatedBitmap, 600, (int) x, true);
        }else
        {
            float x=(float)rotatedBitmap.getWidth()/rotatedBitmap.getHeight()*600;
            bitmap2 = Bitmap.createScaledBitmap(rotatedBitmap,(int) x, 600, true);

        }

        return bitmap2;
    }

    private Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
                        }
                        catch (JSONException e) {
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE ) {
            imageUri = data.getData();
            //image.setImageURI(imageUri);



            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                Bitmap p = getCorrectBitmap(getRealPathFromURI(tempUri),bitmap);

                image.setImageBitmap(p);
                //image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            image.setVisibility(View.VISIBLE);
            image_bitmap = null;
            try
            {
                image_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            image.setVisibility(View.VISIBLE);*/
            setPic();
            image.setVisibility(View.VISIBLE);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////






    String currentPhotoPath;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    static final int REQUEST_TAKE_PHOTO = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.main.citisim.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic()
    {
        // Get the dimensions of the View
        int targetW = image.getWidth();
        int targetH = image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imageToBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        try
        {
            image.setImageBitmap(getCorrectBitmap(currentPhotoPath,imageToBitmap));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    public void viewReports()
    {
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
    }
    public void openQRActivity()
    {
        Intent intent = new Intent(this, QRscanner.class);
        startActivity(intent);
    }
}