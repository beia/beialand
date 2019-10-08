package com.main.citisim.runnables;
import android.content.Context;
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
import java.util.ArrayList;

public class UpdateDeviceRunnable implements Runnable {
    private Context context;
    private int deviceId;

    public UpdateDeviceRunnable(Context context, int deviceId)
    {
        this.context = context;
        this.deviceId = deviceId;
    }

    @Override
    public void run() {
        try
        {
            while (true)
            {
                final String url = context.getResources().getString(R.string.api_altfactor) + "/getlastrecordsdevice/user1";
                final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                ArrayList<LatLng> markerLocation = new ArrayList<>();
                                try
                                {
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
                                            DeviceParameters device = new DeviceParameters(latitude, longitude, cO2, dust, airQuality, speed);

                                            //send the message with the data to the UI thread
                                            Message msg = MapActivity.handler.obtainMessage();
                                            msg.what = 1;
                                            msg.obj = device;
                                            msg.sendToTarget();
                                        }
                                    }
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