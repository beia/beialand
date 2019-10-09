package com.main.citisim;
import java.util.ArrayList;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

public class DeviceData extends AppCompatActivity {

    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;


    CheckBox CO2Box;
    CheckBox HumidityBox;
    CheckBox SpeedBox;
    CheckBox TempBox;
    CheckBox AirQBox;
    CheckBox DustBox;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data);


        getCO2();

        CO2Box= findViewById(R.id.CO2Box);
        DustBox= findViewById(R.id.DustBox);
        HumidityBox = findViewById(R.id.HumidityBox);
        SpeedBox = findViewById(R.id.SpeedBox);
        TempBox = findViewById(R.id.TempBox);
        AirQBox = findViewById(R.id.AirQBox);

        CO2Box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCO2();
                setBoxes(CO2Box);
            }
        });

        DustBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDust();
                setBoxes(DustBox);
            }
        });

        HumidityBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHumidity();
                setBoxes(HumidityBox);
            }
        });

        SpeedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeed();
                setBoxes(SpeedBox);
            }
        });

        TempBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTemperature();
                setBoxes(TempBox);
            }
        });

        AirQBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAirQuality();
                setBoxes(AirQBox);
            }
        });


    }

    public void AddValuesToBARENTRY(ArrayList<Float> co2List) {

      /*  BARENTRY.add(new BarEntry(2f, 0));
        BARENTRY.add(new BarEntry(4f, 1));
        BARENTRY.add(new BarEntry(6f, 2));
        BARENTRY.add(new BarEntry(8f, 3));
        BARENTRY.add(new BarEntry(7f, 4));
        BARENTRY.add(new BarEntry(3f, 5));
        */

        for (int i = 0; i < co2List.size(); i++) {
            BARENTRY.add(new BarEntry(co2List.get(i), i));
        }

    }

    public void AddValuesToBarEntryLabels(ArrayList<Float> co2List, ArrayList<String> timeList) {

        /*BarEntryLabels.add("11:00");
        BarEntryLabels.add("12:00");
        BarEntryLabels.add("13:00");
        BarEntryLabels.add("14:00");
        BarEntryLabels.add("15:00");
        BarEntryLabels.add("16:00");*/

        for (int i = 0; i < co2List.size(); i++) {
            BarEntryLabels.add(timeList.get(i));
        }


    }


    public void getCO2() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;


        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> co2List = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, co2value;
                                co2value = report.getString(6);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);


                                //  }


                                if (co2value== null || co2value=="null") {
                                    co2value = "0";
                                }

                                co2List.add(Float.parseFloat(co2value));


                            }
                            Log.d("lolo", co2List.toString() + "");


                            chart = (BarChart) findViewById(R.id.chart1);



                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {

                                }

                                public void onSwipeRight() {

                                }

                                public void onSwipeLeft() {
                                    chart.clear();
                                    getDust();
                                    CO2Box.setChecked(false);

                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });
                            ;

                            CO2Box.setChecked(true);

                            int colors[] = {R.color.colorAccent};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();

                            AddValuesToBARENTRY(co2List);
                            AddValuesToBarEntryLabels(co2List, timeList);


                            Bardataset = new BarDataSet(BARENTRY, "CO2 (PPM)");

                            BARDATA = new BarData(BarEntryLabels, Bardataset);

                            Bardataset.setColors(ColorTemplate.createColors(colors));


                            chart.setData(BARDATA);
                            chart.setDescription(null);

                            chart.animateY(3000);


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


    public void getDust() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;


        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> dustList = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, dust;
                                dust = report.getString(8);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);

                                //  }

                                if (dust == null || dust=="null") {
                                    dust = "0";
                                }

                                dustList.add(Float.parseFloat(dust));


                            }
                            Log.d("lolo", dustList.toString() + "");

                            DustBox.setChecked(true);

                            chart = (BarChart) findViewById(R.id.chart1);

                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {
                                    Toast.makeText(DeviceData.this, "top", Toast.LENGTH_SHORT).show();
                                }

                                public void onSwipeRight() {
                                    chart.clear();
                                    getCO2();
                                    DustBox.setChecked(false);

                                }

                                public void onSwipeLeft() {
                                    chart.clear();
                                    getHumidity();
                                    DustBox.setChecked(false);
                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });
                            ;


                            int colors[] = {R.color.black};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();

                            AddValuesToBARENTRY(dustList);
                            AddValuesToBarEntryLabels(dustList, timeList);


                            Bardataset = new BarDataSet(BARENTRY, "Dust");

                            BARDATA = new BarData(BarEntryLabels, Bardataset);

                            Bardataset.setColor(ContextCompat.getColor(DeviceData.this, R.color.dust));


                            chart.setData(BARDATA);
                            chart.setDescription(null);

                            chart.animateY(3000);


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


    public void getHumidity() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> dustList = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, dust;
                                dust = report.getString(4);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);

                                //  }


                                if (dust == null || dust=="null") {
                                    dust = "0";
                                }

                                dustList.add(Float.parseFloat(dust));


                            }

                            HumidityBox.setChecked(true);


                            chart = (BarChart) findViewById(R.id.chart1);

                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {
                                    Toast.makeText(DeviceData.this, "top", Toast.LENGTH_SHORT).show();
                                }

                                public void onSwipeRight() {
                                    chart.clear();
                                    getDust();
                                    HumidityBox.setChecked(false);
                                }

                                public void onSwipeLeft() {
                                    chart.clear();
                                    getSpeed();
                                    HumidityBox.setChecked(false);
                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });
                            ;


                            int colors[] = {R.color.black};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();

                            AddValuesToBARENTRY(dustList);
                            AddValuesToBarEntryLabels(dustList, timeList);


                            Bardataset = new BarDataSet(BARENTRY, "Humidity (%)");

                            BARDATA = new BarData(BarEntryLabels, Bardataset);

                            Bardataset.setColor(ContextCompat.getColor(DeviceData.this, R.color.humidity));


                            chart.setData(BARDATA);
                            chart.setDescription(null);

                            chart.animateY(3000);


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


    public void getSpeed() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> speedList = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, speed;
                                speed = report.getString(5);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);

                                //  }
                                if (speed == null || speed=="null") {
                                    speed = "0";
                                }

                                speedList.add(Float.parseFloat(speed));


                            }

                            SpeedBox.setChecked(true);

                            chart = (BarChart) findViewById(R.id.chart1);

                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {
                                    Toast.makeText(DeviceData.this, "top", Toast.LENGTH_SHORT).show();
                                }

                                public void onSwipeRight() {
                                    chart.clear();
                                    getHumidity();
                                    SpeedBox.setChecked(false);

                                }

                                public void onSwipeLeft() {
                                    chart.clear();
                                    getTemperature();
                                    SpeedBox.setChecked(false);

                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });
                            ;


                            int colors[] = {R.color.black};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();



                                AddValuesToBARENTRY(speedList);
                                AddValuesToBarEntryLabels(speedList, timeList);


                                Bardataset = new BarDataSet(BARENTRY, "Speed (Km/h)");

                                BARDATA = new BarData(BarEntryLabels, Bardataset);

                                Bardataset.setColor(ContextCompat.getColor(DeviceData.this, R.color.speed));


                                chart.setData(BARDATA);
                                chart.setDescription(null);


                            chart.animateY(3000);


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


    public void getTemperature() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> speedList = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, speed;
                                speed = report.getString(7);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);

                                //  }

                                if (speed == null || speed=="null") {
                                    speed = "0";
                                }

                                speedList.add(Float.parseFloat(speed));


                            }

                            TempBox.setChecked(true);

                            chart = (BarChart) findViewById(R.id.chart1);

                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {
                                    Toast.makeText(DeviceData.this, "top", Toast.LENGTH_SHORT).show();
                                }

                                public void onSwipeRight() {
                                    chart.clear();
                                    getSpeed();
                                    TempBox.setChecked(false);

                                }

                                public void onSwipeLeft() {
                                    chart.clear();
                                    getAirQuality();
                                    TempBox.setChecked(false);
                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });
                            ;


                            int colors[] = {R.color.black};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();

                            AddValuesToBARENTRY(speedList);
                            AddValuesToBarEntryLabels(speedList, timeList);


                            Bardataset = new BarDataSet(BARENTRY, "Temperature (°C)");

                            BARDATA = new BarData(BarEntryLabels, Bardataset);

                            Bardataset.setColor(ContextCompat.getColor(DeviceData.this, R.color.temperature));

                            chart.setData(BARDATA);
                            chart.setDescription(null);

                            chart.animateY(3000);


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



    public void getAirQuality() {
        final String url = getResources().getString(R.string.api_altfactor) + "/getrecordslasthours/"+MapActivity.deviceSelectedId;

        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            ArrayList<Float> speedList = new ArrayList<Float>();
                            ArrayList<String> timeList = new ArrayList<String>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONArray report = response.getJSONArray(i);

                                // for(int j=0;j < report.length();j++) {
                                String lat, time, speed;
                                speed = report.getString(9);
                                time = report.getString(1);

                                String[] times = time.split(" ", 0);

                                timeList.add(times[1]);

                                //  }

                                if (speed == null || speed=="null") {
                                    speed = "0";
                                }

                                speedList.add(Float.parseFloat(speed));


                            }

                            AirQBox.setChecked(true);

                            chart = (BarChart) findViewById(R.id.chart1);

                            chart.setOnTouchListener(new OnSwipeTouchListener(DeviceData.this) {
                                public void onSwipeTop() {
                                    Toast.makeText(DeviceData.this, "top", Toast.LENGTH_SHORT).show();
                                }

                                public void onSwipeRight() {
                                    chart.clear();
                                    getTemperature();
                                    AirQBox.setChecked(false);

                                }

                                public void onSwipeLeft() {

                                }

                                public void onSwipeBottom() {
                                    Toast.makeText(DeviceData.this, "bottom", Toast.LENGTH_SHORT).show();
                                }

                            });



                            int colors[] = {R.color.black};

                            BARENTRY = new ArrayList<>();

                            BarEntryLabels = new ArrayList<String>();

                            AddValuesToBARENTRY(speedList);
                            AddValuesToBarEntryLabels(speedList, timeList);


                            Bardataset = new BarDataSet(BARENTRY, "Air Quality ");

                            BARDATA = new BarData(BarEntryLabels, Bardataset);

                            Bardataset.setColor(ContextCompat.getColor(DeviceData.this, R.color.airQuality));


                            chart.setData(BARDATA);
                            chart.setDescription(null);

                            chart.animateY(3000);


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

    void setBoxes(CheckBox activeBox){
        if(activeBox!=CO2Box){
            CO2Box.setChecked(false);
        }
        if(activeBox!=DustBox){
            DustBox.setChecked(false);
        }
        if(activeBox!=HumidityBox){
            HumidityBox.setChecked(false);
        }
        if(activeBox!=SpeedBox){
            SpeedBox.setChecked(false);
        }
        if(activeBox!=TempBox){
            TempBox.setChecked(false);
        }
        if(activeBox!=AirQBox){
            AirQBox.setChecked(false);
        }
    }



}