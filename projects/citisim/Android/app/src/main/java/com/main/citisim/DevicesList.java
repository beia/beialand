package com.main.citisim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class DevicesList extends AppCompatActivity {

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        getDevices();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout( width,height);
    }


    public void setLayout(String [] s) {

        final ListAdapter reportAdapter = new ReportAdapter(this, s);
        ListView reportListView = (ListView) findViewById(R.id.reportListView);
        reportListView.setAdapter(reportAdapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(DevicesList.this,"position"+position,Toast.LENGTH_LONG).show();

            }
        });
    }


    public void getDevices() {

        ArrayList<String> devices = new ArrayList<String>();

        devices.add( "Sensor from my car");
        devices.add("Sensor from my bike");
        devices.add("Sensor from my house");
        devices.add( "Sensor from bus");
        devices.add("Sensor from beia");
        devices.add("Sensor from bathroom");
        devices.add( "Sensor from my car");
        devices.add("Sensor from my bike");
        devices.add("Sensor from my house");
        devices.add( "Sensor from bus");
        devices.add("Sensor from beia");
        devices.add("Sensor from bathroom");

        StringBuilder t=new StringBuilder();
        for (int i=0;i<devices.size();i++)
        {
            t.append(devices.get(i)+ "\n");
        }
        String [] s = t.toString().split("\n",0);
        setLayout(s);

    }

}
