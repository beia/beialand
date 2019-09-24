package com.main.citisim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QRscanner extends AppCompatActivity {


    public static int deletePosition=-1;


    private static final String MY_PREFS_NAME = "links" ;
    TextView qrContent;
    ListView historyQRList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

         qrContent=findViewById(R.id.qrContent);
         historyQRList=findViewById(R.id.historyQRList);

        setHistory();

         qrContent.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openPage(qrContent.getText().toString());
             }
         });

        ImageButton qrButton =findViewById(R.id.qrButton);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenQRscanner();
            }
        });



    }

    private void OpenQRscanner(){
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                qrContent.setText(contents);

                saveLink(contents);

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private  void openPage(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    public void setLayout(String [] s) {

        final ListAdapter reportAdapter = new QRadapter(this,s);
        ListView reportListView = (ListView)  findViewById(R.id.historyQRList);
        reportListView.setAdapter(reportAdapter);

        historyQRList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setHistory();

            }
        });



    }




    private void saveLink(String link){

        TinyDB tinydb = new TinyDB(getApplicationContext());

        links=new ArrayList<>();

        links=tinydb.getListString("links");


        if(!links.contains(link))
            links.add(link);
        else {
            links.remove(links.indexOf(link));
            links.add(link);
        }

        tinydb.putListString("links", links);

        String[] s = new String[links.size()];
        s = links.toArray(s);

        setLayout(s);

    }



    private void removeLink(int position){

        TinyDB tinydb = new TinyDB(getApplicationContext());

        links=new ArrayList<>();

        links=tinydb.getListString("links");

        links.remove(position);

        if(deletePosition!=-1){
            removeLink(deletePosition);
            deletePosition=-1;
        }


        tinydb.putListString("links", links);

        String[] s = new String[links.size()];
        s = links.toArray(s);

        setLayout(s);

    }


    ArrayList<String> links;

    public void setHistory(){
        TinyDB tinydb = new TinyDB(getApplicationContext());

         links=new ArrayList<>();

        links=tinydb.getListString("links");


        if(deletePosition!=-1){
            removeLink(deletePosition);
            deletePosition=-1;
        }




        String[] s = new String[links.size()];
        s = links.toArray(s);



        setLayout(s);
    }




}
