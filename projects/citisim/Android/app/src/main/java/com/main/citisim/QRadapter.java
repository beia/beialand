package com.main.citisim;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class QRadapter extends ArrayAdapter<String> {

    int position;

    public void selectedItem(int position)
    {
        this.position = position; //position must be a global variable
    }


    public QRadapter(Context context, String [] details) {
        super(context,R.layout.custom_row,details);
    }





    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater reportsInflater = LayoutInflater.from(getContext());
        final View qrView = reportsInflater.inflate(R.layout.qr_row,parent,false);



        String singleReportItem = getItem(position);
        TextView qrLink = (TextView) qrView.findViewById(R.id.qrLink);
        final Button removeLink =  qrView.findViewById(R.id.removeButton);

        qrLink.setText(singleReportItem);


        //String [] s = ReportsActivity.t.split("\n",0);
        removeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                removeLink(position);

                final ListAdapter reportAdapter = new QRadapter(getContext(),getStringArray());
                ListView reportListView = (ListView)  parent.findViewById(R.id.historyQRList);
                reportListView.setAdapter(reportAdapter);



            }
        });


        Button openLink = qrView.findViewById(R.id.openLink);
        openLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getStringArray()[position])));
            }
        });

        return qrView;
    }



    public void setLayout(String [] s) {





    }

    private void removeLink(int position){

        TinyDB tinydb = new TinyDB(getContext());

        ArrayList links;

        links=new ArrayList<>();

        links=tinydb.getListString("links");

        links.remove(position);


        tinydb.putListString("links", links);

        String[] s = new String[links.size()];
        s = (String[]) links.toArray(s);



    }

    public String[] getStringArray (){
        TinyDB tinydb = new TinyDB(getContext());
        ArrayList links;

        links=new ArrayList<>();

        links=tinydb.getListString("links");

        String[] s = new String[links.size()];
        s = (String[]) links.toArray(s);

        return s;
    }





}
