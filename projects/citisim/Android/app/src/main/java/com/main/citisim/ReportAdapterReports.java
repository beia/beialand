package com.main.citisim;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportAdapterReports extends ArrayAdapter<String> {

    int position;

    public void selectedItem(int position)
    {
        this.position = position; //position must be a global variable
    }


    public ReportAdapterReports(Context context, String [] details) {
        super(context,R.layout.custom_row_reports,details);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater reportsInflater = LayoutInflater.from(getContext());
        View reportView = reportsInflater.inflate(R.layout.custom_row_reports,parent,false);

        String singleReportItem = getItem(position);
        TextView reportTitle = (TextView) reportView.findViewById(R.id.reportTitle);
        ImageView reportImage = (ImageView) reportView.findViewById(R.id.reportImage);
        Button locateOnMap = (Button) reportView.findViewById(R.id.locateOnMap);

        locateOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                ReportsActivity.setReportLocation(position);



                //Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), MapActivity.class);
                v.getContext().startActivity(intent);
            }
        });



        //String [] s = ReportsActivity.t.split("\n",0);
        reportTitle.setText(singleReportItem);
        reportImage.setImageResource(R.drawable.logo);
        return reportView;
    }







}
