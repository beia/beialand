package com.main.citisim.runnables;
import android.content.Context;
import android.os.Debug;
import android.os.Message;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.main.citisim.MapActivity;
import com.main.citisim.MyVolleyQueue;
import com.main.citisim.R;
import com.main.citisim.data.DeviceParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class UpdateDeviceRunnable implements Runnable {
    private Context context;

    public UpdateDeviceRunnable(Context context)
    {
        this.context = context;
    }

    @Override
    public void run() {
        try
        {
            //get the last sensor values so we can display the graph with the previous values as well as the current live values
            // lista cu toate datele transmise de device in tr-un anumit interval de timp
            //http://86.127.100.48:5000/getrecordsperiod/3/2019-04-02%2012:17:52/2019-04-02%2012:18:09
            int yearStart, monthStart, dayStart, hourStart, minuteStart, yearEnd, monthEnd, dayEnd, hourEnd, minuteEnd;
            yearEnd = MapActivity.calendar.get(Calendar.YEAR);
            monthEnd = MapActivity.calendar.get(Calendar.MONTH) + 1;
            dayEnd = MapActivity.calendar.get(Calendar.DAY_OF_MONTH);
            hourEnd = MapActivity.calendar.get(Calendar.HOUR_OF_DAY);
            minuteEnd = MapActivity.calendar.get(Calendar.MINUTE);
            if(minuteEnd - 5 < 0)
            {
                minuteStart = 60 - (5 - minuteEnd);
                if(hourEnd == 0)
                {
                    hourStart = 23;
                    if(dayEnd == 1)
                    {
                        if(monthEnd == 1)
                        {
                            monthStart = 12;
                            dayStart = 31;
                            yearStart = yearEnd - 1;
                        }
                        else
                        {
                            monthStart = monthEnd - 1;
                            int daysInMonth = new GregorianCalendar(yearEnd, monthStart, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
                            dayStart = daysInMonth;
                            yearStart = yearEnd;
                        }
                    }
                    else
                    {
                        dayStart = dayEnd - 1;
                        monthStart = monthEnd;
                        yearStart = yearEnd;
                    }
                }
                else
                {
                    hourStart = hourEnd - 1;
                    dayStart = dayEnd;
                    monthStart = monthEnd;
                    yearStart = yearEnd;
                }
            }
            else
            {
                minuteStart = minuteEnd - 5;
                hourStart = hourEnd;
                dayStart = dayEnd;
                monthStart = monthEnd;
                yearStart = yearEnd;
            }
            String yearStartString, monthStartString, dayStartString, hourStartString, minuteStartString, yearEndString, monthEndString, dayEndString, hourEndString, minuteEndString;
            if(yearStart < 10) yearStartString = "0" + yearStart; else yearStartString = yearStart + "";
            if(monthStart < 10) monthStartString = "0" + monthStart; else monthStartString = monthStart + "";
            if(dayStart < 10) dayStartString = "0" + dayStart; else dayStartString = dayStart + "";
            if(hourStart < 10) hourStartString = "0" + hourStart; else hourStartString = hourStart + "";
            if(minuteStart < 10) minuteStartString = "0" + minuteStart; else minuteStartString = minuteStart + "";
            if(yearEnd < 10) yearEndString = "0" + yearEnd; else yearEndString = yearEnd + "";
            if(monthEnd < 10) monthEndString = "0" + monthEnd; else monthEndString = monthEnd + "";
            if(dayEnd < 10) dayEndString = "0" + dayEnd; else dayEndString = dayEnd + "";
            if(hourEnd < 10) hourEndString = "0" + hourEnd; else hourEndString = hourEnd + "";
            if(minuteEnd < 10) minuteEndString = "0" + minuteEnd; else minuteEndString = minuteEnd + "";

            String url = context.getResources().getString(R.string.api_altfactor) + "/getrecordsperiod/" + MapActivity.deviceSelectedId +"/"+ yearStartString +"-"+ monthStartString +"-"+ dayStartString +"%2012:"+ hourStartString +":"+ minuteStartString +"/"+ yearEndString +"-"+ monthEndString +"-"+ dayEndString +"%2012:"+ hourEndString +":" + minuteEndString;
            JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try
                            {
                                ArrayList<DeviceParameters> deviceParameters = new ArrayList<>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONArray responseJSONArray = response.getJSONArray(i);
                                    int deviceId = responseJSONArray.getInt(0);

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
                                    if(responseJSONArray.getString(6).equals("null") == false)
                                    {
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
                                    DeviceParameters deviceParameter = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                                    deviceParameters.add(deviceParameter);
                                }
                                //send the message with the data to the UI thread
                                Message msg = MapActivity.handler.obtainMessage();
                                msg.what = 2;
                                msg.obj = deviceParameters;
                                msg.sendToTarget();
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
            while (true)
            {
                url = context.getResources().getString(R.string.api_altfactor) + "/getlastrecordsdevice/user1";
                getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                ArrayList<LatLng> markerLocation = new ArrayList<>();
                                try
                                {
                                    DeviceParameters device = null;
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONArray responseJSONArray = response.getJSONArray(i);
                                        int deviceId = responseJSONArray.getInt(0);
                                        if(deviceId == MapActivity.deviceSelectedId)
                                        {
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
                                            device = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);
                                            device.setTime(responseJSONArray.getString(1));
                                        }
                                    }
                                    //send the message with the data to the UI thread
                                    Message msg = MapActivity.handler.obtainMessage();
                                    msg.what = 1;
                                    msg.obj = device;
                                    msg.sendToTarget();
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
                Thread.sleep(5000);
            }
        }
        catch(InterruptedException ex)
        {
            //the user manually closed the thread when selecting another device or when he presses the 'x' button on the gauges
        }
    }
}