package com.main.citisim;

import android.annotation.SuppressLint;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import com.google.android.gms.maps.model.Marker;



class PopupAdapter implements InfoWindowAdapter {

    private View popup=null;

    private LayoutInflater inflater=null;



    PopupAdapter(LayoutInflater inflater) {

        this.inflater=inflater;

    }



    @Override

    public View getInfoWindow(Marker marker) {

        View v = inflater.inflate(R.layout.popup, null);

        TextView airQualityValue=(TextView)v.findViewById(R.id.airQualityValue);
        TextView temperatureValue=(TextView)v.findViewById(R.id.temperatureValue);
        TextView humidityValue=(TextView)v.findViewById(R.id.humidityValue);
        ProgressBar progressBar=(ProgressBar)v.findViewById(R.id.progressBar);


        //airQualityValue.setText(marker.getTitle());

        //tv=(TextView)popup.findViewById(R.id.snippet);
        String []s=marker.getSnippet().split(" ",0);

        airQualityValue.setText(s[0]);
        temperatureValue.setText(s[1]);
        humidityValue.setText(s[2]);
        progressBar.setProgress((int)Double.parseDouble(s[0]));

        //return(null);
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