package com.main.citisim;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportsActivity extends AppCompatActivity {

    public static LatLng reportLocation;
    public static double lat;
    public static double lon;
    public static boolean showReport=false;
    public static ArrayList<LatLng> coordinates;
    ArrayList<String> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        coordinates=new ArrayList<>();
        getReports();
    }


    private void openReportPage(int position)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.api_server) + "/#/entity/report/"+ids.get(position))));
    }


    public void setLayout(String [] s){

        final ListAdapter reportAdapter = new ReportAdapterReports(this,s);
        ListView reportListView = (ListView)  findViewById(R.id.reportListView);
        reportListView.setAdapter(reportAdapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reportLocation= coordinates.get(position);
                //Log.d("verificare",reportLocation.toString());
                lat=reportLocation.latitude;
                lon=reportLocation.longitude;

                /*Intent intent = new Intent(ReportsActivity.this, MapActivity.class);
                startActivity(intent);
                */
                openReportPage(position);
            }
        });


        /*
        reportListView.setOnClickListener(
                new AdapterView.OnClickListener(){


                    @Override
                    public void onClick(View view ) {
                        String details = String.valueOf();
                        Toast.makeText(ReportsActivity.this,details,Toast.LENGTH_LONG ).show();
                    }
                }
        );
        */



    }


    public static void setReportLocation(int position){
        reportLocation= coordinates.get(position);
        showReport=true;
    }



    public void getReports() {
        final String url = getResources().getString(R.string.api_server) + "/api/reports";


        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    StringBuilder text= new StringBuilder();

                    @Override
                    public void onResponse(JSONArray response) {
                        //ArrayList<String> coordinates = new ArrayList<>();

                        ArrayList<String> titles = new ArrayList<>();
                        ArrayList<String> categories = new ArrayList<>();



                        try {
                            //Toast.makeText(ReportsActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                            ArrayList<String> titleslist =new ArrayList<String>();
                            ArrayList<String> descriptionlist =new ArrayList<String>();




                                for (int i = 0; i < response.length(); i++) {
                                   // JSONObject report =null; //response.getJSONObject(i);
                                    JSONObject report=response.getJSONObject(i);
                                    //Log.d("raspuns",report.toString());
                                   // Log.d("raspuns",response.length()+" primul for "+i);

                                    titles.add(report.getString("title"));
                                    ids.add(report.getString("id"));
                                    //titles.add(report.getString("title"));
                                    JSONObject coordinatesObject = report.getJSONObject("location");
                                    coordinates.add (new LatLng(Double.parseDouble(coordinatesObject.getString("lat")),Double.parseDouble(coordinatesObject.getString("lon"))));
                                    categories.add(report.getString("category"));
                                }

                              //  Log.d("raspuns",response.toString());

                                for (int i = 0; i < titles.size(); i++) {

                                   // Log.d("raspuns",response.length()+"");


                                   // text.append(titles.get(i)+" "+coordinates.get(i)+ "\n" +descriptionlist.get(i) + "\n\n");
                                    text.append(titles.get(i)+"\n"+categories.get(i)+"\n\n");
                                }
                            String []  s =text.toString().split("\n\n",0);


                           setLayout(s);

                        } catch (Exception e) {
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
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + new Session(getApplication()).getAuthToken());
                return params;
            }
        };

        // add it to the RequestQueue
        MyVolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }


}
