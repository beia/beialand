package com.main.citisim;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.main.citisim.data.DeviceParameters;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_APPEND;
import static com.main.citisim.MapActivity.MAX_AIRQUALITY;
import static com.main.citisim.MapActivity.MAX_C02;
import static com.main.citisim.MapActivity.MAX_DUST;
import static com.main.citisim.MapActivity.MAX_SPEED;
import static com.main.citisim.MapActivity.pieViewCO2;

public class MarkersHandler extends Handler
{
    private GoogleMap mMap;
    private Bitmap smallMarker;
    private Bitmap firstMarker;
    private Bitmap lastMarker;
    static int counter=1;
    static boolean isRefreshed=false;

    public static ArrayList<Marker> markerVector = new ArrayList<>();

    public MarkersHandler(GoogleMap mMap, Bitmap smallMarker,Bitmap firstMarker,Bitmap lastMarker)
    {
        this.mMap = mMap;
        this.smallMarker = smallMarker;
        this.firstMarker = firstMarker;
        this.lastMarker = lastMarker;
        counter=1;
    }



    @Override
    public void handleMessage(Message msg)
    {

        if (profile.isReadyHistory == true) {
            switch (msg.what) {
                case 1:
                    //adaugam marker
                    final DeviceParameters deviceParameters = (DeviceParameters)msg.obj;
                    LatLng position = new LatLng(deviceParameters.getLatitude(), deviceParameters.getLongitude());
                    if (msg.arg1 == 0)
                    {
                        //move camera over first marker
                        if(mMap!=null)
                            mMap.clear();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MapActivity.DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(firstMarker)).position(position)) ;
                    }

                    if (msg.arg1 == 2)
                    {
                        for(int i= 0; i<markerVector.size();i++){
                            markerVector.get(i).remove();
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MapActivity.DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(lastMarker)).position(position)) ;
                        MapActivity.isDisplayed=false;
                    }
                    else
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MapActivity.DEFAULT_ZOOM));
                        Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position));
                        markerVector.add(marker);
                        if (markerVector.size() > 6)
                        {
                            markerVector.get(0).remove();
                            markerVector.remove(0);
                            markerVector.get(0).setAlpha(0.3f);
                            markerVector.get(1).setAlpha(0.4f);
                            markerVector.get(2).setAlpha(0.5f);
                            markerVector.get(3).setAlpha(0.6f);
                            markerVector.get(4).setAlpha(0.7f);
                            markerVector.get(5).setAlpha(0.8f);
                        }
                    }
                    //show the gauges data
                    float cO2 = (float)Math.round(deviceParameters.getCO2() * 100) / 100;
                    float dust = (float)Math.round(deviceParameters.getDust() * 100) / 100;
                    float airQuality = (float)Math.round(deviceParameters.getAirQuality() * 100) / 100;
                    float speed = (float)Math.round(deviceParameters.getSpeed() * 100) / 100;
                    MapActivity.pieViewCO2.setPercentage(cO2 / MAX_C02 * 100);
                    MapActivity.pieViewCO2.setInnerText(cO2 + "");
                    MapActivity.pieViewDust.setPercentage(dust / MAX_DUST * 100);
                    MapActivity.pieViewDust.setInnerText(dust + "");
                    MapActivity.pieViewAirQuality.setPercentage(airQuality / MAX_AIRQUALITY * 100);
                    MapActivity.pieViewAirQuality.setInnerText(airQuality + "");
                    MapActivity.pieViewSpeed.setPercentage(speed / MAX_SPEED * 100);
                    MapActivity.pieViewSpeed.setInnerText(speed + "");

                    //set the listeners for the gauges so we can see the graphs associated with the coresponding sensor measurements
                    pieViewCO2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            XAxis xAxis = MapActivity.lineChartCO2.getXAxis();
                            xAxis.enableGridDashedLine(10f, 10f, 0f);
                            YAxis yAxis = MapActivity.lineChartCO2.getAxisLeft();
                            // disable dual axis (only use LEFT axis)
                            MapActivity.lineChartCO2.getAxisRight().setEnabled(false);
                            yAxis.enableGridDashedLine(10f, 10f, 0f);
                            // axis range
                            yAxis.setAxisMaximum(200f);
                            yAxis.setAxisMinimum(-50f);
                        }
                    });
                break;
            }
        }
    }
}