package com.main.citisim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ReportAdapter extends ArrayAdapter<String> {

    int position;

    public void selectedItem(int position)
    {
        this.position = position; //position must be a global variable
    }


    public ReportAdapter(Context context, String [] details) {
        super(context,R.layout.custom_row,details);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater reportsInflater = LayoutInflater.from(getContext());
        View reportView = reportsInflater.inflate(R.layout.custom_row,parent,false);

        String singleReportItem = getItem(position);
        TextView reportTitle = (TextView) reportView.findViewById(R.id.reportTitle);
        ImageView reportImage = (ImageView) reportView.findViewById(R.id.reportImage);


        //String [] s = ReportsActivity.t.split("\n",0);
        reportTitle.setText(singleReportItem);
        reportImage.setImageResource(R.drawable.logo);
        return reportView;
    }







}
