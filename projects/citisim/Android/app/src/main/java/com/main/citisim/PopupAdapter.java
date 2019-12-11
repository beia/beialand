package com.main.citisim;

import android.annotation.SuppressLint;

import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import com.google.android.gms.maps.model.Marker;
import com.main.citisim.data.DeviceParameters;


class PopupAdapter implements InfoWindowAdapter {

    private View popup=null;

    private LayoutInflater inflater=null;



    PopupAdapter(LayoutInflater inflater) {

        this.inflater=inflater;

    }



    @Override

    public View getInfoWindow(Marker marker) {
        View v = null;
        DeviceParameters deviceParameter = null;
        if (marker.getTag() instanceof DeviceParameters)
            deviceParameter = (DeviceParameters) marker.getTag();
        if(deviceParameter != null) {
            switch (deviceParameter.usecase) {
                case "Analytics":
                    double parameterValue;
                    switch (MapActivity.parameterName)
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
                    Toast.makeText(MapActivity.context, MapActivity.parameterName + ": " + parameterValue, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
        else
        {
            v = inflater.inflate(R.layout.popup, null);
            TextView airQualityValue = (TextView) v.findViewById(R.id.airQualityValue);
            TextView temperatureValue = (TextView) v.findViewById(R.id.temperatureValue);
            TextView humidityValue = (TextView) v.findViewById(R.id.humidityValue);
            ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            //airQualityValue.setText(marker.getTitle());
            //tv=(TextView)popup.findViewById(R.id.snippet);
            String[] s = marker.getSnippet().split(" ", 0);
            airQualityValue.setText(s[0]);
            temperatureValue.setText(s[1]);
            humidityValue.setText(s[2]);
            progressBar.setProgress((int) Double.parseDouble(s[0]));
        }
        return v;
    }



    @SuppressLint("InflateParams")

    @Override

    public View getInfoContents(Marker marker) {

       /* if (popup == null) {

            popup=inflater.inflate(R.layout.popup, null);
            //popup.setBackgroundColor();

        }
        */
        return null;

    }

}