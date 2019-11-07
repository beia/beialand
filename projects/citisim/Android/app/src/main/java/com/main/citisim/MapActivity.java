package com.main.citisim;

import android.Manifest;
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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.main.citisim.data.DeviceParameters;
import com.main.citisim.runnables.UpdateDeviceRunnable;
import com.main.citisim.runnables.UpdateMarkers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import az.plainpie.PieView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    static boolean isRun=false;
    static boolean isDisplayed=false;
    final HashMap info = new HashMap();
    public static MarkersHandler markersHandler;
    static ArrayList<DeviceParameters> markerLocations = new ArrayList<>();
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    public static GoogleMap mMap;
    private ClusterManager<MarkerClusterItem> mClusterManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static double latitude;
    public static double longitude;
    private HeatmapTileProvider mProvider;
    public static String alfactorApiString;
    public static Context context;

    //UI variables
    public static CardView gaugesCardView;
    public static PieView pieViewCO2;
    public static PieView pieViewDust;
    public static PieView pieViewAirQuality;
    public static PieView pieViewSpeed;

    //marker variables
    public static BitmapDrawable markerBitmapDrawable;
    public static Bitmap markerBitmap;
    public static Bitmap smallMarker;
    public static Bitmap firstMarker;
    public static Bitmap lastMarker;
    public static int markerHeight = 130;
    public static int markerWidth = 120;

    //History variables
    public static volatile boolean historyThreadFinished = true;
    public static String startDate;
    public static String endDate;

    //time variables
    public static Calendar calendar;

    //My devices variables
    public static volatile boolean deviceSelected = false;
    public static volatile int deviceSelectedId;
    public static final int MAX_C02 = 600;
    public static final int MAX_DUST = 10000;
    public static final int MAX_AIRQUALITY = 100;
    public static final int MAX_SPEED = 200;
    public static Marker deviceMarker;
    public Thread updateDeviceData;

    //threads
    public static Thread updateDevice;


    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1://update the gauges
                    DeviceParameters device = (DeviceParameters) msg.obj;
                    float cO2 = (float)Math.round(device.getCO2() * 100) / 100;
                    float dust = (float)Math.round(device.getDust() * 100) / 100;
                    float airQuality = (float)Math.round(device.getAirQuality() * 100) / 100;
                    float speed = (float)Math.round(device.getSpeed() * 100) / 100;
                    pieViewCO2.setPercentage(cO2 / MAX_C02 * 100);
                    pieViewCO2.setInnerText(cO2 + "");
                    pieViewDust.setPercentage(dust / MAX_DUST * 100);
                    pieViewDust.setInnerText(dust + "");
                    pieViewAirQuality.setPercentage(airQuality / MAX_AIRQUALITY * 100);
                    pieViewAirQuality.setInnerText(airQuality + "");
                    pieViewSpeed.setPercentage(speed / MAX_SPEED * 100);
                    pieViewSpeed.setInnerText(speed + "");

                    //show the device on the map
                    if(mMap != null)
                        mMap.clear();
                    if(deviceMarker != null)
                        deviceMarker.remove();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(MapActivity.markerBitmap, markerWidth, markerHeight, false);
                    LatLng devicePosition = new LatLng(device.getLatitude(), device.getLongitude());
                    deviceMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(devicePosition));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(devicePosition, DEFAULT_ZOOM));

                    Log.d("[GAUGES HANDLER]: ", " device parameters: {CO2: " + device.getCO2() + " Dust: " + device.getDust() + " Air quality: " + device.getAirQuality() + " Speed: " + device.getSpeed() + "}");
                    break;
            }
        }
    };




    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (mLocationPermissionsGranted)
        {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Location permission not gruanteed.", Toast.LENGTH_LONG).show();
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.clear();
            getReports();
            getSensors();
            showReport();
            getDeviceLocation();
        }
        markersHandler = new MarkersHandler(mMap, smallMarker,firstMarker,lastMarker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        context = getApplicationContext();
        alfactorApiString = getResources().getString(R.string.api_altfactor);
        getLocationPermission();
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewReport();
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        final ImageButton profile = (ImageButton) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this,profile.class));
            }
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mMap.clear();
                getReports();
                getSensors();
                getDeviceLocation();
            }
        });

        if(mMap!=null && ReportsActivity.showReport==true)
        {
            showReport();
        }

        //create a marker
        markerBitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        markerBitmap = markerBitmapDrawable.getBitmap();
        //create the markers needed for showing the history
        int height = 90;
        int width = 85;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        Bitmap b=bitmapdraw.getBitmap();
        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.firstmarkerr);
        Bitmap c=bitmapdraw1.getBitmap();
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.lastmarkerr);
        Bitmap d=bitmapdraw2.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        firstMarker = Bitmap.createScaledBitmap(c,width,height,false);
        lastMarker = Bitmap.createScaledBitmap(d,width,height,false);


        //init the gauges
        gaugesCardView = findViewById(R.id.gauges);
        pieViewCO2 = findViewById(R.id.pieViewCO2);
        pieViewDust = findViewById(R.id.pieViewDust);
        pieViewAirQuality = findViewById(R.id.pieViewAirQuality);
        pieViewSpeed = findViewById(R.id.pieViewSpeed);
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



    private void updateHeatMap(List<LatLng> list)
    {
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

    public void getSensors()
    {
        final String url = getResources().getString(R.string.api_altfactor)+"/getlastrecordsdevice/user1";



        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<LatLng> markerLocation = new ArrayList<>();
                        try
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                JSONArray report = response.getJSONArray(i);
                                String lat,lon,airQuality,humidity,temperature;
                                lat=report.getString(2);
                                lon=report.getString(3);
                                airQuality=report.getString(12);
                                humidity=report.getString(4);
                                temperature=report.getString(7);

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
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
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
        if(mMap!=null && ReportsActivity.showReport==true )
        {
            showReport();
        }

        //DEVICES
        //if a device was selected we show the gauges and the device name on the bottom of the screen
        //we create a thread that will check for new data from the device that was clicked at every 5 seconds
        if(deviceSelected)
        {
            gaugesCardView.setVisibility(View.VISIBLE);
            updateDevice = new Thread(new UpdateDeviceRunnable(getApplicationContext()));
            updateDevice.start();
            deviceSelected = false;
        }
    }


    public void showReport()
    {
        if(ReportsActivity.showReport==true)
        {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ReportsActivity.reportLocation, DEFAULT_ZOOM));
        }
    }


    //HISTORY
    public static void getSensorLocations()//get sensor locations and show them
    {
        String url=null;
        if(mMap!=null)
            mMap.clear();
        if(MapActivity.startDate==null || MapActivity.endDate==null)
        {
            //get the current date and set the uncompleted fields for the user
            calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String currentDate = year + "-" + month + "-" + day;
            if (MapActivity.startDate == null)
            {
                startDate = currentDate;
                endDate = currentDate;
            }
            if (MapActivity.endDate == null)
            {
                endDate = currentDate;
            }
        }

        //the api is not working properly so we create a mock-up url
        url ="http://86.127.100.48:5000/getrecordsperiod/3/2019-04-02 12:19:40/2019-04-02 12:20:13";
        String desiredURL= alfactorApiString + "/getrecordsperiod/" + MapActivity.deviceSelectedId + "/" + MapActivity.startDate + " 12:19:40/" + MapActivity.endDate + " 12:20:13";
        Log.d("URL", desiredURL);
        markerLocations.clear();

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                JSONArray responseJSONArray = response.getJSONArray(i);
                                float latitude, longitude, cO2, dust, airQuality, speed;
                                if(responseJSONArray.getString(2).equals("null") == false)
                                {
                                    latitude = Float.parseFloat(responseJSONArray.getString(2));
                                }
                                else
                                {
                                    latitude = -1;
                                }
                                if(responseJSONArray.getString(3).equals("null") == false)
                                {
                                    longitude = Float.parseFloat(responseJSONArray.getString(3));
                                }
                                else
                                {
                                    longitude = -1;
                                }
                                if(responseJSONArray.getString(6).equals("null") == false) {
                                    cO2 = Float.parseFloat(responseJSONArray.getString(6));
                                }
                                else
                                {
                                    cO2 = -1;
                                }
                                if(responseJSONArray.getString(8).equals("null") == false)
                                {
                                    dust = Float.parseFloat(responseJSONArray.getString(8));
                                }
                                else
                                {
                                    dust = -1;
                                }
                                if(responseJSONArray.getString(12).equals("null") == false)
                                {
                                    airQuality = Float.parseFloat(responseJSONArray.getString(12));
                                }
                                else
                                {
                                    airQuality = -1;
                                }
                                if(responseJSONArray.getString(5).equals("null") == false)
                                {
                                    speed = Float.parseFloat(responseJSONArray.getString(5));
                                }
                                else
                                {
                                    speed = -1;
                                }
                                DeviceParameters deviceParameters = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                                markerLocations.add(deviceParameters);
                            }
                            //stop the real time display of the device so we can see the history
                            if(updateDevice != null && updateDevice.isAlive())
                                updateDevice.interrupt();
                            showMarkers();
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
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MyVolleyQueue.getInstance(context).addToRequestQueue(getRequest);
    }


    public static  void showMarkers()
    {
        if(profile.isReadyHistory==true)
        {
            //start the markers thread
            Thread showMarkersThread = new Thread(new UpdateMarkers(markerLocations));
            showMarkersThread.start();
            //show the gauges that show us informations about some measured parameters
            gaugesCardView.setVisibility(View.VISIBLE);
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
