package com.main.citisim;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import az.plainpie.PieView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{//, GoogleMap.OnMarkerClic {

    static boolean isRun=false;
    static boolean isDisplayed=false;
    final HashMap info = new HashMap();
    public static MarkersHandler markersHandler;
    static ArrayList<DeviceParameters> markerLocations = new ArrayList<>();
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static final float DEFAULT_ZOOM = 15f;
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
    public static LinearLayout gaugesLinearLayout;
    public static PieView pieViewCO2;
    public static PieView pieViewDust;
    public static PieView pieViewAirQuality;
    public static PieView pieViewSpeed;
    public static boolean showGraphsDevices = false;
    public static LinearLayout graphsLinearLayout;
    public static LineChart lineChartCO2;
    public static LineChart lineChartDust;
    public static LineChart lineChartAirQuality;
    public static LineChart lineChartSpeed;
    public static TextView historyTimeTextView;


    //chache variables
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;

    //marker variables
    public static BitmapDrawable markerBitmapDrawable;
    public static Bitmap markerBitmap;
    public static Bitmap analyticsGreenMarker;
    public static Bitmap analyticsRedMarker;
    public static Bitmap smallMarker;
    public static Bitmap firstMarker;
    public static Bitmap lastMarker;
    public static int markerWidth = 120;
    public static int markerHeight = 130;
    public static int analyticsMarkerWidth = 50;
    public static int analyticsMarkerHeight = 50;

    //History variables
    public static volatile boolean historyThreadFinished = true;
    public static String startDate;
    public static String endDate;
    //Analytics variables
    public static volatile boolean analyticsThreadFinished = true;
    public static String startDateAnalytics;
    public static String endDateAnalytics;
    public static String parameterName;
    public static double threshold;
    public static volatile boolean zoom = false;

    //time variables
    public static Calendar calendar;

    //My devices variables
    public static volatile boolean deviceSelected = false;
    public static volatile int deviceSelectedId;
    public static final int MAX_C02 = 600;
    public static final int MAX_DUST = 10000;
    public static final int MAX_AIRQUALITY = 140;
    public static final int MAX_SPEED = 200;
    public static Marker deviceMarker;
    public Thread updateDeviceData;

    //Analytics variables
    public static volatile Integer selectedParameterAnalytics;
    public static volatile Integer parameterThreshold;

    //threads
    public static Thread updateDevice;
    public static Thread displayHistoryThread;


    public static Handler handler = new Handler(){
        ArrayList<Entry> cO2Values = new ArrayList<>();
        ArrayList<Entry> dustValues = new ArrayList<>();
        ArrayList<Entry> airQualityValues = new ArrayList<>();
        ArrayList<Entry> speedValues = new ArrayList<>();
        LineDataSet cO2Set, dustSet, airQualitySet, speedSet;
        LineData cO2LineData, dustLineData, airQualityLineData, speedLineData;
        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 1:
                    //update the gauges(receiveing the last record from the device)
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

                    //set the time
                    String badTime = device.getTime();
                    String[] dateHour = badTime.split(" ");
                    String[] monthDayYear = dateHour[0].split("/");
                    String time = monthDayYear[1] + "/" + monthDayYear[0] + "/" + monthDayYear[2] + " " + dateHour[1];
                    historyTimeTextView.setText(time);

                    //update the graphs
                    //remove the first element from the parameters array
                    //update all the (x, y) points from the graph by translating the x values to the left by one
                    //add the value received in the array
                    if(cO2Values.size() > 60) {
                        cO2Values.remove(0);
                        for (Entry point : cO2Values)
                            point.setX(point.getX() - 1);
                    }
                    cO2Values.add(new Entry(cO2Values.size(), device.getCO2()));
                    if(dustValues.size() > 60) {
                        dustValues.remove(0);
                        for (Entry point : dustValues)
                            point.setX(point.getX() - 1);
                    }
                    dustValues.add(new Entry(dustValues.size(), device.getDust()));
                    if(airQualityValues.size() > 60) {
                        airQualityValues.remove(0);
                        for (Entry point : airQualityValues)
                            point.setX(point.getX() - 1);
                    }
                    airQualityValues.add(new Entry(airQualityValues.size(), device.getAirQuality()));
                    if(speedValues.size() > 60) {
                        speedValues.remove(0);
                        for (Entry point : speedValues)
                            point.setX(point.getX() - 1);
                    }
                    speedValues.add(new Entry(speedValues.size(), device.getSpeed()));
                    //update the data sets
                    if(cO2Set != null)
                        cO2Set.setValues(cO2Values);
                    if(dustSet != null)
                        dustSet.setValues(dustValues);
                    if(airQualitySet != null)
                        airQualitySet.setValues(airQualityValues);
                    if(speedSet != null)
                        speedSet.setValues(speedValues);
                    //update the line data objects
                    cO2LineData = new LineData(cO2Set);
                    dustLineData = new LineData(dustSet);
                    airQualityLineData = new LineData(airQualitySet);
                    speedLineData = new LineData(speedSet);
                    //update the graph charts
                    lineChartCO2.setData(cO2LineData);
                    lineChartCO2.notifyDataSetChanged();
                    lineChartCO2.invalidate();
                    lineChartDust.setData(dustLineData);
                    lineChartDust.notifyDataSetChanged();
                    lineChartDust.invalidate();
                    lineChartAirQuality.setData(airQualityLineData);
                    lineChartAirQuality.notifyDataSetChanged();
                    lineChartAirQuality.invalidate();
                    lineChartSpeed.setData(speedLineData);
                    lineChartSpeed.notifyDataSetChanged();
                    lineChartSpeed.invalidate();

                    //show the device on the map
                    if(mMap != null)
                        mMap.clear();
                    if(deviceMarker != null)
                        deviceMarker.remove();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(MapActivity.markerBitmap, markerWidth, markerHeight, false);
                    LatLng devicePosition = new LatLng(device.getLatitude(), device.getLongitude());
                    deviceMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(devicePosition));
                    if(!zoom)//zoom in only the first time so the user can zoom out if he wants
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(devicePosition, DEFAULT_ZOOM));
                        zoom = true;
                    }
                    else
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(devicePosition));
                    }
                    Log.d("[GAUGES HANDLER]: ", " device parameters: {CO2: " + device.getCO2() + " Dust: " + device.getDust() + " Air quality: " + device.getAirQuality() + " Speed: " + device.getSpeed() + "}");
                    break;
                case 2://receiving the records from the last 500 seconds(to have 100 samples)
                    historyTimeTextView.setVisibility(View.VISIBLE);
                    ArrayList<DeviceParameters> deviceParameters = (ArrayList<DeviceParameters>) msg.obj;
                    for(int i = 0; i < deviceParameters.size(); i++)
                    {
                        cO2Values.add(new Entry(i, deviceParameters.get(i).getCO2()));
                        dustValues.add(new Entry(i, deviceParameters.get(i).getDust()));
                        airQualityValues.add(new Entry(i, deviceParameters.get(i).getAirQuality()));
                        speedValues.add(new Entry(i, deviceParameters.get(i).getSpeed()));
                    }
                    // init the cO2 dataset
                    cO2Set = new LineDataSet(cO2Values, "CO2");
                    cO2Set.setAxisDependency(YAxis.AxisDependency.LEFT);
                    cO2Set.setColor(ColorTemplate.getHoloBlue());
                    cO2Set.setValueTextColor(ColorTemplate.getHoloBlue());
                    cO2Set.setLineWidth(2f);
                    cO2Set.setDrawCircles(false);
                    cO2Set.setDrawValues(false);
                    cO2Set.setFillAlpha(65);
                    cO2Set.setFillColor(ColorTemplate.getHoloBlue());
                    cO2Set.setHighLightColor(Color.rgb(244, 117, 117));
                    cO2Set.setDrawCircleHole(false);
                    // init the dust dataset
                    dustSet = new LineDataSet(dustValues, "Dust");
                    dustSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    dustSet.setColor(ColorTemplate.getHoloBlue());
                    dustSet.setValueTextColor(ColorTemplate.getHoloBlue());
                    dustSet.setLineWidth(2f);
                    dustSet.setDrawCircles(false);
                    dustSet.setDrawValues(false);
                    dustSet.setFillAlpha(65);
                    dustSet.setFillColor(ColorTemplate.getHoloBlue());
                    dustSet.setHighLightColor(Color.rgb(244, 117, 117));
                    dustSet.setDrawCircleHole(false);
                    // init the airQuality dataset
                    airQualitySet = new LineDataSet(airQualityValues, "Air Quality");
                    airQualitySet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    airQualitySet.setColor(ColorTemplate.getHoloBlue());
                    airQualitySet.setValueTextColor(ColorTemplate.getHoloBlue());
                    airQualitySet.setLineWidth(2f);
                    airQualitySet.setDrawCircles(false);
                    airQualitySet.setDrawValues(false);
                    airQualitySet.setFillAlpha(65);
                    airQualitySet.setFillColor(ColorTemplate.getHoloBlue());
                    airQualitySet.setHighLightColor(Color.rgb(244, 117, 117));
                    airQualitySet.setDrawCircleHole(false);
                    // init the speed dataset
                    speedSet= new LineDataSet(speedValues, "Speed");
                    speedSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    speedSet.setColor(ColorTemplate.getHoloBlue());
                    speedSet.setValueTextColor(ColorTemplate.getHoloBlue());
                    speedSet.setLineWidth(2f);
                    speedSet.setDrawCircles(false);
                    speedSet.setDrawValues(false);
                    speedSet.setFillAlpha(65);
                    speedSet.setFillColor(ColorTemplate.getHoloBlue());
                    speedSet.setHighLightColor(Color.rgb(244, 117, 117));
                    speedSet.setDrawCircleHole(false);
                    // cO2 line data
                    cO2LineData = new LineData(cO2Set);
                    cO2LineData.setValueTextColor(Color.WHITE);
                    cO2LineData.setValueTextSize(9f);
                    // dust line data
                    dustLineData = new LineData(dustSet);
                    dustLineData.setValueTextColor(Color.WHITE);
                    dustLineData.setValueTextSize(9f);
                    // airQuality line data
                    airQualityLineData = new LineData(airQualitySet);
                    airQualityLineData.setValueTextColor(Color.WHITE);
                    airQualityLineData.setValueTextSize(9f);
                    // speed line data
                    speedLineData = new LineData(speedSet);
                    speedLineData.setValueTextColor(Color.WHITE);
                    speedLineData.setValueTextSize(9f);
                    //cO2 chart data
                    lineChartCO2.setData(cO2LineData);
                    lineChartCO2.notifyDataSetChanged();
                    lineChartCO2.invalidate();
                    //dust chart data
                    lineChartDust.setData(dustLineData);
                    lineChartDust.notifyDataSetChanged();
                    lineChartDust.invalidate();
                    //airQuality chart data
                    lineChartAirQuality.setData(airQualityLineData);
                    lineChartAirQuality.notifyDataSetChanged();
                    lineChartAirQuality.invalidate();
                    //speed chart data
                    lineChartSpeed.setData(speedLineData);
                    lineChartSpeed.notifyDataSetChanged();
                    lineChartSpeed.invalidate();
                    break;
            }
        }
    };




    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        //mMap.setOnMarkerClickListener(this);
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

        //init cache
        sharedPref = this.getPreferences(MODE_PRIVATE);


        //get the calendar instance
        calendar = Calendar.getInstance();

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
        BitmapDrawable greenMarkerBitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.green_dot);
        analyticsGreenMarker = Bitmap.createScaledBitmap(greenMarkerBitmapDrawable.getBitmap(), analyticsMarkerWidth, analyticsMarkerHeight, false);
        BitmapDrawable redMarkerBitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.red_dot);
        analyticsRedMarker = Bitmap.createScaledBitmap(redMarkerBitmapDrawable.getBitmap(), analyticsMarkerWidth, analyticsMarkerHeight, false);
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
        gaugesLinearLayout = findViewById(R.id.gaugesLinearLayout);
        pieViewCO2 = findViewById(R.id.pieViewCO2);
        pieViewDust = findViewById(R.id.pieViewDust);
        pieViewAirQuality = findViewById(R.id.pieViewAirQuality);
        pieViewSpeed = findViewById(R.id.pieViewSpeed);
        //init the graphs
        graphsLinearLayout = findViewById(R.id.graphsLineaLayout);
        lineChartCO2 = findViewById(R.id.CO2Graph);
        lineChartDust = findViewById(R.id.DustGraph);
        lineChartAirQuality = findViewById(R.id.AirQualityGraph);
        lineChartSpeed = findViewById(R.id.SpeedGraph);
        //init the time for the history display
        historyTimeTextView = findViewById(R.id.historyTimeTextView);


        //setup the graphs
        lineChartCO2.setBackgroundColor(Color.WHITE);
        lineChartCO2.getDescription().setEnabled(false);
        lineChartCO2.setTouchEnabled(true);
        lineChartCO2.setDrawGridBackground(false);
        lineChartCO2.getAxisLeft().setDrawGridLines(false);
        lineChartCO2.getAxisRight().setDrawGridLines(false);
        lineChartCO2.getAxisRight().setDrawLabels(false);
        lineChartCO2.getXAxis().setDrawGridLines(false);
        lineChartCO2.getXAxis().setDrawLabels(false);
        lineChartDust.setBackgroundColor(Color.WHITE);
        lineChartDust.getDescription().setEnabled(false);
        lineChartDust.setTouchEnabled(true);
        lineChartDust.setDrawGridBackground(false);
        lineChartDust.getAxisLeft().setDrawGridLines(false);
        lineChartDust.getAxisRight().setDrawGridLines(false);
        lineChartDust.getAxisRight().setDrawLabels(false);
        lineChartDust.getXAxis().setDrawGridLines(false);
        lineChartDust.getXAxis().setDrawLabels(false);
        lineChartAirQuality.setBackgroundColor(Color.WHITE);
        lineChartAirQuality.getDescription().setEnabled(false);
        lineChartAirQuality.setTouchEnabled(true);
        lineChartAirQuality.setDrawGridBackground(false);
        lineChartAirQuality.getAxisLeft().setDrawGridLines(false);
        lineChartAirQuality.getAxisRight().setDrawGridLines(false);
        lineChartAirQuality.getAxisRight().setDrawLabels(false);
        lineChartAirQuality.getXAxis().setDrawGridLines(false);
        lineChartAirQuality.getXAxis().setDrawLabels(false);
        lineChartSpeed.setBackgroundColor(Color.WHITE);
        lineChartSpeed.getDescription().setEnabled(false);
        lineChartSpeed.setTouchEnabled(true);
        lineChartSpeed.setDrawGridBackground(false);
        lineChartSpeed.getAxisLeft().setDrawGridLines(false);
        lineChartSpeed.getAxisRight().setDrawGridLines(false);
        lineChartSpeed.getAxisRight().setDrawLabels(false);
        lineChartSpeed.getXAxis().setDrawGridLines(false);
        lineChartSpeed.getXAxis().setDrawLabels(false);

        gaugesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showGraphsDevices)
                {
                    graphsLinearLayout.setVisibility(View.INVISIBLE);
                    showGraphsDevices = false;
                }
                else
                {
                    graphsLinearLayout.setVisibility(View.VISIBLE);
                    showGraphsDevices = true;
                }
            }
        });
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
            zoom = false;//set the zoom to false so we cam zoom only on the first marker displayed
            //stop the real time display of a device
            if(updateDevice != null && updateDevice.isAlive())
                updateDevice.interrupt();
            //stop the real time display of the history so we can see the real time device display
            if(displayHistoryThread != null && displayHistoryThread.isAlive())
                displayHistoryThread.interrupt();
            historyThreadFinished = true;
            profile.setIsReadyHistory(false);
            gaugesLinearLayout.setVisibility(View.VISIBLE);
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

        String historyData = sharedPref.getString(startDate + endDate, null);
        if(historyData == null) {
            //the api is not working properly so we create a mock-up url
            url = "http://86.127.100.48:5000/getrecordsperiod/3/2019-04-02 12:19:40/2019-04-02 12:20:13";
            String desiredURL = alfactorApiString + "/getrecordsperiod/" + MapActivity.deviceSelectedId + "/" + MapActivity.startDate + " 00:00:00/" + MapActivity.endDate + " 23:59:59";
            Log.d("MOCKUP URL", url);
            Log.d("URL", desiredURL);
            markerLocations.clear();

            final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, desiredURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //save the history data into the cache memory
                            editor = sharedPref.edit();
                            editor.putString(startDate + endDate, response.toString());
                            editor.commit();
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONArray responseJSONArray = response.getJSONArray(i);
                                    float latitude, longitude, cO2, dust, airQuality, speed;
                                    if (responseJSONArray.getString(2).equals("null") == false) {
                                        latitude = Float.parseFloat(responseJSONArray.getString(2));
                                    } else {
                                        latitude = -1;
                                    }
                                    if (responseJSONArray.getString(3).equals("null") == false) {
                                        longitude = Float.parseFloat(responseJSONArray.getString(3));
                                    } else {
                                        longitude = -1;
                                    }
                                    if (responseJSONArray.getString(6).equals("null") == false) {
                                        cO2 = Float.parseFloat(responseJSONArray.getString(6));
                                    } else {
                                        cO2 = -1;
                                    }
                                    if (responseJSONArray.getString(8).equals("null") == false) {
                                        dust = Float.parseFloat(responseJSONArray.getString(8));
                                    } else {
                                        dust = -1;
                                    }
                                    if (responseJSONArray.getString(12).equals("null") == false) {
                                        airQuality = Float.parseFloat(responseJSONArray.getString(12));
                                    } else {
                                        airQuality = -1;
                                    }
                                    if (responseJSONArray.getString(5).equals("null") == false) {
                                        speed = Float.parseFloat(responseJSONArray.getString(5));
                                    } else {
                                        speed = -1;
                                    }
                                    DeviceParameters deviceParameters = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                                    deviceParameters.setUsecase("History");
                                    deviceParameters.setTime(responseJSONArray.getString(1));
                                    markerLocations.add(deviceParameters);
                                }
                                //stop the real time display of the device so we can see the history
                                if (updateDevice != null && updateDevice.isAlive())
                                    updateDevice.interrupt();
                                showMarkers();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            MyVolleyQueue.getInstance(context).addToRequestQueue(getRequest);
        }
        else
        {
            try
            {
                JSONArray historyDataJsonArray = new JSONArray(historyData);
                for (int i = 0; i < historyDataJsonArray.length(); i++) {
                    JSONArray responseJSONArray = historyDataJsonArray.getJSONArray(i);
                    float latitude, longitude, cO2, dust, airQuality, speed;
                    if (responseJSONArray.getString(2).equals("null") == false) {
                        latitude = Float.parseFloat(responseJSONArray.getString(2));
                    } else {
                        latitude = -1;
                    }
                    if (responseJSONArray.getString(3).equals("null") == false) {
                        longitude = Float.parseFloat(responseJSONArray.getString(3));
                    } else {
                        longitude = -1;
                    }
                    if (responseJSONArray.getString(6).equals("null") == false) {
                        cO2 = Float.parseFloat(responseJSONArray.getString(6));
                    } else {
                        cO2 = -1;
                    }
                    if (responseJSONArray.getString(8).equals("null") == false) {
                        dust = Float.parseFloat(responseJSONArray.getString(8));
                    } else {
                        dust = -1;
                    }
                    if (responseJSONArray.getString(12).equals("null") == false) {
                        airQuality = Float.parseFloat(responseJSONArray.getString(12));
                    } else {
                        airQuality = -1;
                    }
                    if (responseJSONArray.getString(5).equals("null") == false) {
                        speed = Float.parseFloat(responseJSONArray.getString(5));
                    } else {
                        speed = -1;
                    }
                    DeviceParameters deviceParameters = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                    deviceParameters.setUsecase("History");
                    deviceParameters.setTime(responseJSONArray.getString(1));
                    markerLocations.add(deviceParameters);
                }
                //stop the real time display of the device so we can see the history
                if (updateDevice != null && updateDevice.isAlive())
                    updateDevice.interrupt();
                showMarkers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static  void showMarkers()
    {
        if(profile.isReadyHistory==true)
        {
            zoom = false;
            //start the markers thread
            displayHistoryThread = new Thread(new UpdateMarkers(markerLocations));
            displayHistoryThread.start();
            //show the gauges that show us informations about some measured parameters
            gaugesLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    //ANALYTICS
    public static void getSensorLocationsAnalytics()//get sensor locations and show them
    {
        String url=null;
        if(mMap!=null)
            mMap.clear();
        if(MapActivity.startDateAnalytics==null || MapActivity.endDateAnalytics==null)
        {
            //get the current date and set the uncompleted fields for the user
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String currentDate = year + "-" + month + "-" + day;
            if (MapActivity.startDate == null)
            {
                startDateAnalytics = currentDate;
                endDateAnalytics = currentDate;
            }
            if (MapActivity.endDateAnalytics == null)
            {
                endDateAnalytics = currentDate;
            }
        }

        String analyticsData = sharedPref.getString(startDateAnalytics + endDateAnalytics, null);
            //the api is not working properly so we create a mock-up url
            url = "http://86.127.100.48:5000/getrecordsperiod/3/2019-04-02 12:19:40/2019-04-02 12:20:13";
            String desiredURL = alfactorApiString + "/getrecordsperiod/" + MapActivity.deviceSelectedId + "/" + MapActivity.startDateAnalytics + " 00:00:00/" + MapActivity.endDateAnalytics + " 23:59:59";
            Log.d("URL", desiredURL);
            markerLocations.clear();

            final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, desiredURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONArray responseJSONArray = response.getJSONArray(i);
                                    float latitude, longitude, cO2, dust, airQuality, speed;
                                    if (responseJSONArray.getString(2).equals("null") == false) {
                                        latitude = Float.parseFloat(responseJSONArray.getString(2));
                                    } else {
                                        latitude = -1;
                                    }
                                    if (responseJSONArray.getString(3).equals("null") == false) {
                                        longitude = Float.parseFloat(responseJSONArray.getString(3));
                                    } else {
                                        longitude = -1;
                                    }
                                    if (responseJSONArray.getString(6).equals("null") == false) {
                                        cO2 = Float.parseFloat(responseJSONArray.getString(6));
                                    } else {
                                        cO2 = -1;
                                    }
                                    if (responseJSONArray.getString(8).equals("null") == false) {
                                        dust = Float.parseFloat(responseJSONArray.getString(8));
                                    } else {
                                        dust = -1;
                                    }
                                    if (responseJSONArray.getString(12).equals("null") == false) {
                                        airQuality = Float.parseFloat(responseJSONArray.getString(12));
                                    } else {
                                        airQuality = -1;
                                    }
                                    if (responseJSONArray.getString(5).equals("null") == false) {
                                        speed = Float.parseFloat(responseJSONArray.getString(5));
                                    } else {
                                        speed = -1;
                                    }
                                    DeviceParameters deviceParameters = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                                    deviceParameters.setUsecase("Analytics");
                                    deviceParameters.setTime(responseJSONArray.getString(1));
                                    markerLocations.add(deviceParameters);
                                }
                                //stop the real time display of the device so we can see the history
                                if (updateDevice != null && updateDevice.isAlive())
                                    updateDevice.interrupt();
                                //stop the real time display of the history so we can see the analytics
                                if (displayHistoryThread != null && displayHistoryThread.isAlive())
                                    displayHistoryThread.interrupt();
                                historyThreadFinished = true;
                                gaugesLinearLayout.setVisibility(View.GONE);
                                showMarkersAnalytics();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            MyVolleyQueue.getInstance(context).addToRequestQueue(getRequest);
    }
    public static void showMarkersAnalytics()
    {
            for(int i = 0; i < markerLocations.size(); i++)
            {
                Float parameterValue;
                DeviceParameters deviceParameter = markerLocations.get(i);
                switch(parameterName)
                {
                    case "CO2":
                        parameterValue = deviceParameter.getCO2();
                        break;
                    case "Dust":
                        parameterValue = deviceParameter.getDust();
                        break;
                    case "AirQuality":
                        parameterValue = deviceParameter.getAirQuality();
                        break;
                    case "Speed":
                        parameterValue = deviceParameter.getSpeed();
                        break;
                    default:
                        parameterValue = -1f;
                        break;
                }
                LatLng devicePosition = new LatLng(deviceParameter.getLatitude(), deviceParameter.getLongitude());
                if(parameterValue < threshold)
                    deviceMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(analyticsGreenMarker)).position(devicePosition));
                else
                    deviceMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(analyticsRedMarker)).position(devicePosition));
                deviceMarker.setTag(deviceParameter);
                if(i != markerLocations.size() - 1) {
                    if(parameterValue < threshold) {
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(deviceParameter.getLatitude(), deviceParameter.getLongitude()), new LatLng(markerLocations.get(i + 1).getLatitude(), markerLocations.get(i + 1).getLongitude()))
                                .width(10)
                                .color(Color.GREEN));
                    }
                    else
                    {
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(deviceParameter.getLatitude(), deviceParameter.getLongitude()), new LatLng(markerLocations.get(i + 1).getLatitude(), markerLocations.get(i + 1).getLongitude()))
                                .width(10)
                                .color(Color.RED));
                    }
                }
                if(i == 1)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(devicePosition, 12));
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
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                if (ReportsActivity.showReport == true) {
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            DEFAULT_ZOOM);
                                    showReport();
                                    ReportsActivity.showReport = false;
                                } else {
                                    moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM);
                                }
                            }
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
    public void getCameraPermission()
    {
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }
    public void getGalleryPermision()
    {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED)
        {
            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[] { permission },
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
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
                    getCameraPermission();
                }
                break;
            case CAMERA_PERMISSION_CODE:
                getGalleryPermision();
                break;
        }
    }

    /*
    @Override
    public boolean onMarkerClick(Marker marker) {
        DeviceParameters deviceParameter = null;
        if (marker.getTag() instanceof DeviceParameters)
            deviceParameter = (DeviceParameters) marker.getTag();
        if(deviceParameter != null) {
            switch (deviceParameter.usecase) {
                case "Analytics":
                    double parameterValue;
                    switch (parameterName)
                    {
                        case "CO2":
                            parameterValue = deviceParameter.getCO2();
                            break;
                        case "Dust":
                            parameterValue = deviceParameter.getDust();
                            break;
                        case "AirQuality":
                            parameterValue = deviceParameter.getAirQuality();
                            break;
                        case "Speed":
                            parameterValue = deviceParameter.getSpeed();
                            break;
                        default:
                            parameterValue = -1;
                            break;
                    }
                    Toast.makeText(context, parameterName + ": " + parameterValue, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    */
}
