package com.main.citisim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class profile extends AppCompatActivity {

    //TableRow row1 = (TableRow) findViewById(R.id.row1);

    //public static MarkersHandler startRunHandler;

    boolean isVisibleDevices=false;
    boolean isVisibleDevice =false;
    boolean isVisibleHistory=false;
    boolean isVisibleEnd=false;
    boolean isVisibleStart=false;
    boolean isVisibleRun=false;
    static  boolean isReadyHistory=false;
    boolean isVisibleDeviceAnalytics =false;
    boolean isVisibleAnalytics=false;
    boolean isVisibleEndAnalytics=false;
    boolean isVisibleStartAnalytics=false;
    boolean isVisibleRunAnalytics=false;
    boolean isVisibleParameters = false;
    static  boolean isReadyAnalytics=false;

    public static LinearLayout parametersLinearLayout;
    public static CheckBox cO2Checkbox;
    public static CheckBox dustCheckbox;
    public static CheckBox airQualityCheckbox;
    public static CheckBox speedCheckbox;
    public static EditText thresholdEditText;

    public ArrayList<Integer> deviceIdList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        deviceIdList=new ArrayList<>();


        final ScrollView deviceList = (ScrollView)findViewById(R.id.deviceList);
        final ScrollView historyList = (ScrollView)findViewById(R.id.historyList);
        final ScrollView analyticsList = (ScrollView)findViewById(R.id.analyticsList);

        final ListView reportListView=(ListView)findViewById(R.id.reportListView);
        final ListView historyListView= (ListView)findViewById(R.id.historyListView);
        final ListView analyticsListView = findViewById(R.id.analyticsListView);

        final TableRow deviceHistory = (TableRow)findViewById(R.id.deviceHistory);
        final TableRow deviceAnalytics = (TableRow)findViewById(R.id.deviceAnalytics);
        final TableRow startHistory = (TableRow)findViewById(R.id.startHistory);
        final TableRow startAnalytics = (TableRow)findViewById(R.id.startAnalytics);
        final TableRow endHistory= (TableRow)findViewById(R.id.endHistory);
        final TableRow endAnalytics = (TableRow)findViewById(R.id.endAnalytics);
        final TableRow runRow = (TableRow) findViewById(R.id.runRow);
        final TableRow runRowAnalytics = (TableRow) findViewById(R.id.runRowAnalytics);
        final TableRow parameterAnalytics = findViewById(R.id.parameterAnalytics);

        final CalendarView calendarStart = (CalendarView)findViewById(R.id.calendarStart);
        final CalendarView calendarEnd = (CalendarView)findViewById(R.id.calendarEnd);
        final CalendarView calendarStartAnalytics = (CalendarView) findViewById((R.id.calendarStartAnalytics));
        final CalendarView calendarEndAnalytics = (CalendarView) findViewById((R.id.calendarEndAnalytics));

        parametersLinearLayout = findViewById(R.id.parameterLinearLayout);
        cO2Checkbox = findViewById(R.id.CO2Checkbox);
        dustCheckbox = findViewById(R.id.DustCheckbox);
        airQualityCheckbox = findViewById(R.id.AirQualityCheckbox);
        speedCheckbox = findViewById(R.id.SpeedCheckbox);
        thresholdEditText = findViewById(R.id.thresholdEditText);

        final TextView myDevicesText = (TextView) findViewById(R.id.myDevicesText);

        deviceList.setVisibility(View.GONE);
        historyList.setVisibility(View.GONE);
        deviceHistory.setVisibility(View.GONE);
        startHistory.setVisibility(View.GONE);
        calendarStart.setVisibility(View.GONE);
        calendarEnd.setVisibility(View.GONE);
        endHistory.setVisibility(View.GONE);
        runRow.setVisibility(View.GONE);
        analyticsList.setVisibility(View.GONE);
        deviceAnalytics.setVisibility(View.GONE);
        startAnalytics.setVisibility(View.GONE);
        calendarStartAnalytics.setVisibility(View.GONE);
        calendarEndAnalytics.setVisibility(View.GONE);
        endAnalytics.setVisibility(View.GONE);
        runRowAnalytics.setVisibility(View.GONE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setGravity(Gravity.RIGHT);

       final int width = dm.widthPixels;
       final int height = dm.heightPixels;

        getWindow().setLayout( (int)(width*0.4),height);
        getWindow().setGravity(Gravity.RIGHT);

        //DEVICES
        ImageButton myDevices = (ImageButton)findViewById(R.id.myDevices);
        myDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(profile.this,"o sa mearga",Toast.LENGTH_LONG).show();
                if(isVisibleDevices==false)
                {
                    closeAll(deviceList,historyList);
                    getDevices2(reportListView);
                    deviceList.setVisibility(View.VISIBLE);
                    isVisibleDevices=true;
                    getWindow().setLayout( (int)(width*0.4),height);
                }else{
                    deviceList.setVisibility(View.GONE);
                    isVisibleDevices=false;
                }

                if(isVisibleHistory==true)
                {
                    deviceHistory.setVisibility(View.GONE);
                    startHistory.setVisibility(View.GONE);
                    isVisibleHistory=false;
                    endHistory.setVisibility(View.GONE);
                    runRow.setVisibility(View.GONE);
                }

            }
        });

        ImageButton historyButton = (ImageButton)findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleHistory==false)
                {
                    closeAll(deviceList,historyList);
                    deviceHistory.setVisibility(View.VISIBLE);
                    startHistory.setVisibility(View.VISIBLE);
                    endHistory.setVisibility(View.VISIBLE);
                    runRow.setVisibility(View.VISIBLE);
                    isVisibleHistory=true;

                    //getWindow().setLayout( (int)(width*0.4),height);
                }else{
                    deviceHistory.setVisibility(View.GONE);
                    startHistory.setVisibility(View.GONE);
                    isVisibleHistory=false;
                    endHistory.setVisibility(View.GONE);
                    runRow.setVisibility(View.GONE);
                }
            }
        });

        ImageButton analyticsButton = (ImageButton)findViewById(R.id.analyticsButton);
        analyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleAnalytics==false)
                {
                    closeAll(deviceList,analyticsList);
                    deviceAnalytics.setVisibility(View.VISIBLE);
                    startAnalytics.setVisibility(View.VISIBLE);
                    endAnalytics.setVisibility(View.VISIBLE);
                    parameterAnalytics.setVisibility(View.VISIBLE);
                    runRowAnalytics.setVisibility(View.VISIBLE);
                    isVisibleAnalytics=true;

                    //getWindow().setLayout( (int)(width*0.4),height);
                }else{
                    deviceAnalytics.setVisibility(View.GONE);
                    startAnalytics.setVisibility(View.GONE);
                    endAnalytics.setVisibility(View.GONE);
                    runRowAnalytics.setVisibility(View.GONE);
                    parameterAnalytics.setVisibility(View.GONE);
                    isVisibleAnalytics=false;
                }
            }
        });
        final ImageButton device = (ImageButton) findViewById(R.id.device);
        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleDevice == false)
                {
                    closeAll(deviceList,historyList);
                    getDevicesHistory(historyListView);
                    historyList.setVisibility(View.VISIBLE);

                    calendarStart.setVisibility(View.GONE);
                    calendarEnd.setVisibility(View.GONE);

                    changeWidth("small");

                    isVisibleEnd=false;
                    isVisibleStart=false;
                    isVisibleDevice =true;
                    //getWindow().setLayout( (int)(width*0.4),height);
                }else{
                    historyList.setVisibility(View.GONE);
                    isVisibleDevice =false;
                }


            }
        });

        final ImageButton deviceAnalyticsButton = (ImageButton) findViewById(R.id.devicesAnalyticsImageButton);
        deviceAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleDeviceAnalytics == false)
                {
                    closeAll(deviceList,analyticsList);
                    getDevicesAnalytics(analyticsListView);
                    analyticsList.setVisibility(View.VISIBLE);

                    calendarStartAnalytics.setVisibility(View.GONE);
                    calendarEndAnalytics.setVisibility(View.GONE);

                    changeWidth("small");

                    isVisibleEndAnalytics=false;
                    isVisibleStartAnalytics=false;
                    isVisibleDeviceAnalytics =true;
                    //getWindow().setLayout( (int)(width*0.4),height);
                }else{
                    analyticsList.setVisibility(View.GONE);
                    isVisibleDeviceAnalytics = false;
                }


            }
        });

       final ImageButton startButton = (ImageButton)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleStart ==false)
                {
                    closeAll(deviceList,historyList);

                    calendarStart.setVisibility(View.VISIBLE);
                    calendarEnd.setVisibility(View.GONE);

                    //calendarStart.set


                    isVisibleStart =true;
                    isVisibleEnd=false;
                    changeWidth("large");


                }else{
                    historyList.setVisibility(View.GONE);
                    calendarStart.setVisibility(View.GONE);
                    isVisibleStart =false;
                    changeWidth("small");
                }
            }
        });
        final ImageButton startButtonAnalytics= (ImageButton)findViewById(R.id.startButtonAnalytics);
        startButtonAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleStartAnalytics ==false)
                {
                    closeAll(deviceList, analyticsList);

                    calendarStartAnalytics.setVisibility(View.VISIBLE);
                    calendarEndAnalytics.setVisibility(View.GONE);

                    //calendarStart.set


                    isVisibleStartAnalytics = true;
                    isVisibleEndAnalytics = false;
                    changeWidth("large");


                }else{
                    analyticsList.setVisibility(View.GONE);
                    calendarStartAnalytics.setVisibility(View.GONE);
                    isVisibleStartAnalytics =false;
                    changeWidth("small");
                }
            }
        });

        final ImageButton endButton = (ImageButton)findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleEnd ==false)
                {
                    closeAll(deviceList,historyList);

                    calendarEnd.setVisibility(View.VISIBLE);
                    //calendarStart.set
                    calendarStart.setVisibility(View.GONE);

                    isVisibleEnd = true;
                    isVisibleStart = false;
                    changeWidth("large");
                }else{
                    calendarEnd.setVisibility(View.GONE);
                    isVisibleEnd = false;
                    changeWidth("small");
                }
            }
        });
        final ImageButton endButtonAnalytics = (ImageButton)findViewById(R.id.endButtonAnalytics);
        endButtonAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleEndAnalytics ==false)
                {
                    closeAll(deviceList,analyticsList);

                    calendarEndAnalytics.setVisibility(View.VISIBLE);
                    //calendarStart.set
                    calendarStartAnalytics.setVisibility(View.GONE);

                    isVisibleEndAnalytics = true;
                    isVisibleStartAnalytics = false;
                    changeWidth("large");
                }else{
                    calendarEndAnalytics.setVisibility(View.GONE);
                    isVisibleEndAnalytics = false;
                    changeWidth("small");
                }
            }
        });


        ImageButton runButton = (ImageButton) findViewById(R.id.runButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleRun == false && MapActivity.historyThreadFinished == true)
                {
                    isReadyHistory=true;
                    closeAll(deviceList,historyList);
                    calendarEnd.setVisibility(View.GONE);
                    calendarStart.setVisibility(View.GONE);
                    isVisibleRun =true;
                    changeWidth("small");
                    MapActivity.getSensorLocations();
                    MapActivity.historyThreadFinished = false;
                    finish();
                }
                else {
                    if(!MapActivity.historyThreadFinished) {
                        historyList.setVisibility(View.GONE);
                        calendarEnd.setVisibility(View.GONE);
                        isVisibleRun = false;
                        changeWidth("small");
                    }
                }
            }
        });
        ImageButton parameterButtonAnalytics = findViewById(R.id.parameterButtonAnalytics);
        parameterButtonAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleParameters == false)
                {
                    closeAll(deviceList, analyticsList);
                    parametersLinearLayout.setVisibility(View.VISIBLE);
                    isVisibleParameters = true;
                    changeWidth("small");
                }
                else
                {
                    parametersLinearLayout.setVisibility(View.GONE);
                    isVisibleParameters = false;
                    changeWidth("small");
                }
            }
        });
        ImageButton runButtonAnalytics = (ImageButton) findViewById(R.id.runButtonAnalytics);
        runButtonAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MapActivity.analyticsThreadFinished == true)
                {
                    boolean correctDataFormat = true;
                    double threshold = 0;
                    if(MapActivity.selectedParameterAnalytics != null)
                    {
                        String thresholdString = thresholdEditText.getText().toString();
                        if(thresholdString != "")
                        {
                            try
                            {
                                threshold = Double.parseDouble(thresholdString);
                            }
                            catch (NumberFormatException ex)
                            {
                                correctDataFormat = false;
                            }
                        }
                        else
                        {
                            correctDataFormat = false;
                        }
                    }
                    else
                    {
                        correctDataFormat = false;
                    }
                    if(correctDataFormat)
                    {
                        MapActivity.threshold = threshold;
                        isReadyAnalytics = true;
                        closeAll(deviceList, analyticsList);
                        calendarEndAnalytics.setVisibility(View.GONE);
                        calendarStartAnalytics.setVisibility(View.GONE);
                        isVisibleRunAnalytics = true;
                        changeWidth("small");
                        MapActivity.getSensorLocationsAnalytics();
                        MapActivity.analyticsThreadFinished = false;
                        MapActivity.selectedParameterAnalytics = null;
                        finish();
                    }
                }
            }
        });



        calendarStart.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

               // Toast.makeText(profile.this, year+ "/ "+(month+1)+"/ "+dayOfMonth,Toast.LENGTH_LONG).show();
                if(month+1<10 && dayOfMonth<10)
                    MapActivity.startDate=year+ "-"+"0"+(month+1)+"-"+"0"+dayOfMonth;
                else
                    if(month+1<10 && dayOfMonth>=10)
                    MapActivity.startDate=year+ "-"+"0"+(month+1)+"-"+dayOfMonth;
                    else
                    if(month+1>=10 && dayOfMonth<10)
                        MapActivity.startDate=year+ "-"+(month+1)+"-"+"0"+dayOfMonth;
                    else
                        MapActivity.startDate=year+ "-"+(month+1)+"-"+dayOfMonth;

            }
        });

        calendarEnd.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                //Toast.makeText(profile.this, year+ "/ "+(month+1)+"/ "+dayOfMonth,Toast.LENGTH_LONG).show();

                if(month+1<10 && dayOfMonth<10)
                    MapActivity.endDate=year+ "-"+"0"+(month+1)+"-"+"0"+dayOfMonth;
                else
                if(month+1<10 && dayOfMonth>=10)
                    MapActivity.endDate=year+ "-"+"0"+(month+1)+"-"+dayOfMonth;
                else
                if(month+1>=10 && dayOfMonth<10)
                    MapActivity.endDate=year+ "-"+(month+1)+"-"+"0"+dayOfMonth;
                else
                    MapActivity.endDate=year+ "-"+(month+1)+"-"+dayOfMonth;

            }
        });
        calendarStartAnalytics.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                // Toast.makeText(profile.this, year+ "/ "+(month+1)+"/ "+dayOfMonth,Toast.LENGTH_LONG).show();
                if(month+1<10 && dayOfMonth<10)
                    MapActivity.startDateAnalytics=year+ "-"+"0"+(month+1)+"-"+"0"+dayOfMonth;
                else
                if(month+1<10 && dayOfMonth>=10)
                    MapActivity.startDateAnalytics=year+ "-"+"0"+(month+1)+"-"+dayOfMonth;
                else
                if(month+1>=10 && dayOfMonth<10)
                    MapActivity.startDateAnalytics=year+ "-"+(month+1)+"-"+"0"+dayOfMonth;
                else
                    MapActivity.startDateAnalytics=year+ "-"+(month+1)+"-"+dayOfMonth;

            }
        });

        calendarEndAnalytics.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                //Toast.makeText(profile.this, year+ "/ "+(month+1)+"/ "+dayOfMonth,Toast.LENGTH_LONG).show();

                if(month+1<10 && dayOfMonth<10)
                    MapActivity.endDateAnalytics=year+ "-"+"0"+(month+1)+"-"+"0"+dayOfMonth;
                else
                if(month+1<10 && dayOfMonth>=10)
                    MapActivity.endDateAnalytics=year+ "-"+"0"+(month+1)+"-"+dayOfMonth;
                else
                if(month+1>=10 && dayOfMonth<10)
                    MapActivity.endDateAnalytics=year+ "-"+(month+1)+"-"+"0"+dayOfMonth;
                else
                    MapActivity.endDateAnalytics=year+ "-"+(month+1)+"-"+dayOfMonth;

            }
        });
    }

    public void onCheckboxClicked(View view)
    {
        boolean checked = ((CheckBox)view).isChecked();
        MapActivity.selectedParameterAnalytics = view.getId();
        switch(view.getId())
        {
            case R.id.CO2Checkbox:
                MapActivity.parameterName = "CO2";
                cO2Checkbox.setChecked(true);
                dustCheckbox.setChecked(false);
                airQualityCheckbox.setChecked(false);
                speedCheckbox.setChecked(false);
                break;
            case R.id.DustCheckbox:
                MapActivity.parameterName = "Dust";
                cO2Checkbox.setChecked(false);
                dustCheckbox.setChecked(true);
                airQualityCheckbox.setChecked(false);
                speedCheckbox.setChecked(false);
                break;
            case R.id.AirQualityCheckbox:
                MapActivity.parameterName = "AirQuality";
                cO2Checkbox.setChecked(false);
                dustCheckbox.setChecked(false);
                airQualityCheckbox.setChecked(true);
                speedCheckbox.setChecked(false);
                break;
            case R.id.SpeedCheckbox:
                MapActivity.parameterName = "Speed";
                cO2Checkbox.setChecked(false);
                dustCheckbox.setChecked(false);
                airQualityCheckbox.setChecked(false);
                speedCheckbox.setChecked(true);
                break;
            default:
                break;
        }
    }


    public void closeAll(ScrollView a,ScrollView b ){

        a.setVisibility(View.GONE);
        isVisibleDevices=false;

        b.setVisibility(View.GONE);
        isVisibleDevice =false;


    }

    public void changeWidth(String x){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setGravity(Gravity.RIGHT);

        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        if(x=="large") {

            View view = findViewById(R.id.layoutId);
            view.getLayoutParams().width = (int) (width * 0.7);


            getWindow().setLayout((int) (width * 0.7), height);
        }else{

           // View view = findViewById(R.id.layoutId);
           // view.getLayoutParams().width = (int) (width * 0.7);


            getWindow().setLayout((int) (width * 0.4), height);
        }

    }

    //DEVICES
    public void setLayout(ArrayList s,ListView v) {

        final ListAdapter reportAdapter = new ArrayAdapter<>(this,R.layout.devicelist_profile,s);
        ListView reportListView = v;//(ListView) findViewById(R.id.reportListView);
        reportListView.setAdapter(reportAdapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(MapActivity.historyThreadFinished) {
                    MapActivity.deviceSelectedId = deviceIdList.get(position);
                    MapActivity.deviceSelected = true;
                    finish();
                }
            }
        });


    }


    public void setLayoutHistory(ArrayList s,ListView v) {

        final ListAdapter reportAdapter = new ArrayAdapter<>(this,R.layout.devicelist_profile,s);
        ListView reportListView = v;//(ListView) findViewById(R.id.reportListView);
        reportListView.setAdapter(reportAdapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapActivity.deviceSelectedId = deviceIdList.get(position);
            }
        });
    }
    public void setLayoutAnalytics(ArrayList s,ListView v) {

        final ListAdapter reportAdapter = new ArrayAdapter<>(this,R.layout.devicelist_profile,s);
        ListView reportListView = v;//(ListView) findViewById(R.id.reportListView);
        reportListView.setAdapter(reportAdapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapActivity.deviceSelectedId = deviceIdList.get(position);
            }
        });
    }




    public void getDevices2(final ListView v){
        final String url = getResources().getString(R.string.api_altfactor)+"/getuserdevices/user1/1";



        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<String> names = new ArrayList<String>();
                        try
                        {
                            deviceIdList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);
                                String name;
                                name=report.getString(7);
                                names.add(name);
                                deviceIdList.add(Integer.parseInt(report.getString(5)));
                            }
                            setLayout(names,v);
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
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }


    public void getDevicesHistory(final ListView v){
        final String url = getResources().getString(R.string.api_altfactor)+"/getuserdevices/user1/1";

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<String> names = new ArrayList<String>();
                        try
                        {
                            deviceIdList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);
                                String name;
                                name=report.getString(7);
                                deviceIdList.add(Integer.parseInt(report.getString(5)));
                                names.add(name);
                            }
                            setLayoutHistory(names,v);
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
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }
    public void getDevicesAnalytics(final ListView v){
        final String url = getResources().getString(R.string.api_altfactor)+"/getuserdevices/user1/1";

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<String> names = new ArrayList<String>();
                        try
                        {
                            deviceIdList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);
                                String name;
                                name=report.getString(7);
                                deviceIdList.add(Integer.parseInt(report.getString(5)));
                                names.add(name);
                            }
                            setLayoutAnalytics(names,v);
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
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }


    public void getDeviceLocations(){


    }

    public static void setIsReadyHistory(boolean t){
        isReadyHistory=t;
    }




}