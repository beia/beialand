package com.main.citisim;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.main.citisim.runnables.UpdateMarkers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    static boolean isRun=false;

    static boolean isDisplayed=false;

    final HashMap info = new HashMap();

    public static MarkersHandler markersHandler;

    static ArrayList<LatLng> markerLocations = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Location permission not gruanteed.", Toast.LENGTH_LONG).show();
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings(). different settings

            mMap.getUiSettings().setCompassEnabled(true);

            // Get the reports
            /*
            getReports();
            getSensors();
            getSensorLocations();
            setUpClusterer();
            */

            mMap.clear();
            getReports();
            getSensors();

            //setUpClusterer();
            //getDeviceLocation();


                showReport();

        }



    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private ClusterManager<MarkerClusterItem> mClusterManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public static double latitude;
    public static double longitude;


    private HeatmapTileProvider mProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();
        ImageButton button2;
        button2 = (ImageButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewReport();
            }
        });

        ImageButton settings;
        settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        final ImageButton profile;
        profile = (ImageButton) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this,profile.class));
            }
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
       // refreshButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getReports();
                getSensors();
              //  getSensorLocations();
               // setUpClusterer();
                getDeviceLocation();
                //isDisplayed=false;

              //  Toast.makeText(MapActivity.this, com.main.citisim.profile.deviceId,Toast.LENGTH_LONG).show();

            }
        });



        if(mMap!=null && ReportsActivity.showReport==true ){
            showReport();
            Log.d("savedem","da");
        }



    }




    private void setUpClusterer() {
        // Position the map.
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MarkerClusterItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MarkerClusterItem offsetItem = new MarkerClusterItem(lat,lng,"","");
            mClusterManager.addItem(offsetItem);
        }
    }




    ///////////////////////////////////////////////////////



    private void openSettings() {

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }



    private void updateHeatMap(List<LatLng> list) {
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));



/*
        LatLng beia =  new LatLng(44.395730,26.102831);
        LatLng beia2 =  new LatLng(44.421659,26.104840);
        LatLng beia3 =  new LatLng(44.401998,26.078392);
        int height = 100;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet("air quality : 63").position(beia).title("sensor 1"));
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet("air quality : 63").position(beia2).title("sensor 2"));
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet("air quality : 63").position(beia3).title("sensor 3"));
        */

    }


////////////////////////////////////////////////////////////

    public void getSensors() {
        final String url = getResources().getString(R.string.api_altfactor)+"/getlastrecordsdevice/user1";



        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<LatLng> markerLocation = new ArrayList<>();
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                               // for(int j=0;j < report.length();j++) {
                                String lat,lon,airQuality,humidity,temperature;
                                lat=report.getString(2);
                                lon=report.getString(3);
                                airQuality=report.getString(12);
                                humidity=report.getString(4);
                                temperature=report.getString(7);


                              //  }
                                markerLocation.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));

                                int height = 130;
                                int width = 120;
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                               // mClusterManager.addItem(new MarkerClusterItem( Double.parseDouble(lat),Double.parseDouble(lon),"","") );
                               // mClusterManager.addItem(new MarkerClusterItem( Double.parseDouble(lat),Double.parseDouble(lon),"","") );

                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet(Math.round(Double.parseDouble(airQuality)*100d)/100d+" "+Math.round(Double.parseDouble(temperature)*100d)/100d+" "+Math.round(Double.parseDouble(humidity)*100d)/100d).position(markerLocation.get(i)).title("sensor 1"));



                                mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);

    }

///////////////////////////////////////

    @Override
    public void onResume(){
        super.onResume();




        if(isDisplayed==false){

            if(mMap!=null)
            mMap.clear();
            getSensorLocations();
            isDisplayed=true;
        }

        if(mMap!=null && ReportsActivity.showReport==true ){
            showReport();
            Log.d("savedem","da");
        }





       // getSensorLocations();
       // Toast.makeText(this,profile.startDate, Toast.LENGTH_LONG).show();

    }


    public void showReport(){
        if(ReportsActivity.showReport==true){

                //Log.d("verif", new LatLng(ReportsActivity.lat, ReportsActivity.lon).toString() + ReportsActivity.showReport);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ReportsActivity.reportLocation, DEFAULT_ZOOM));
                //ReportsActivity.showReport = false;

            Log.d("verif",ReportsActivity.reportLocation.toString() + ReportsActivity.showReport);
        }
    }

    public void getSensorLocations(){
       // final String url = getResources().getString(R.string.api_altfactor)+"/getrecordsperiod/3/"+profile.startDate+"12:17:52/"+profile.endDate+"12:18:09";
        String url=null;

        if(mMap!=null)
        mMap.clear();

        if(profile.startDate==null || profile.endDate==null || profile.deviceId==null)
        {
             url ="http://86.127.100.48:5000/getrecordsperiod/3/2019-04-02 12:19:40/2019-04-02 12:20:13";
        }
        else{
              url = getResources().getString(R.string.api_altfactor)+"/getrecordsperiod/"+profile.deviceId+"/"+profile.startDate+" 12:19:40/"+profile.endDate+" 12:20:13";
            Log.d("merge1",url);
        }

       // String url ="http://86.127.100.48:5000/getrecordsperiod/3/"+profile.startDate+ " 12:19:40/"+profile.endDate+" 12:20:13";

        Log.d("merge1",url);
        //Log.d("merge2",url2);

      //  Toast.makeText(this,profile.startDate,Toast.LENGTH_LONG).show();

        markerLocations.clear();

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int height = 90;
                        int width = 85;

                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
                        Bitmap b=bitmapdraw.getBitmap();
                        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.firstmarkerr);
                        Bitmap c=bitmapdraw1.getBitmap();
                        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.lastmarkerr);
                        Bitmap d=bitmapdraw2.getBitmap();

                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        Bitmap firstMarker = Bitmap.createScaledBitmap(c,width,height,false);
                        Bitmap lastMarker = Bitmap.createScaledBitmap(d,width,height,false);

                        try
                        {
                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);
                                String lat,lon;
                                lat=report.getString(2);
                                lon=report.getString(3);
                                markerLocations.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                            }

                            Log.d("catevalori",markerLocations.toString());



                            if(markersHandler!=null){
                                markersHandler=null;
                            }


                            markersHandler = new MarkersHandler(mMap, smallMarker,firstMarker,lastMarker);
                            /*Thread showMarkersThread = new Thread(new UpdateMarkers(markerLocations));
                            showMarkersThread.start();*/




                            showMarkers();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);



    }


    public static  void showMarkers(){
        if(profile.isReadyHistory==true){


            Thread showMarkersThread = new Thread(new UpdateMarkers(markerLocations));
            showMarkersThread.start();
            Log.d("locatii",markerLocations.toString());
        }
    }


    public void getReports() {
        final String url = getResources().getString(R.string.api_server) + "/api/reports";


        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<LatLng> coordinates = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject report = response.getJSONObject(i);


                                Double lat = Double.parseDouble(report.getJSONObject("location").getString("lat"));
                                Double lng = Double.parseDouble(report.getJSONObject("location").getString("lon"));



                                coordinates.add(new LatLng(lat, lng));


                            }



                            updateHeatMap(coordinates);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
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


    public void openNewReport() {
        Intent intent = new Intent(this, NewReport.class);
        startActivity(intent);
    }





    public void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();


                            if(ReportsActivity.showReport==true){
                                /*moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);*/
                                showReport();
                                ReportsActivity.showReport=false;
                            }else{
                                moveCamera(new LatLng(latitude,longitude),DEFAULT_ZOOM);
                            }

                            Log.d("mda","123");



                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void refreshMap(){


        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }


    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && UpdateMarkers.speedTime<=800){
            UpdateMarkers.speedTime+=50;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && UpdateMarkers.speedTime>=100){
            UpdateMarkers.speedTime-=50;
        }

        return true;
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);

            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();


                }

            }

        }
    }


}
