package com.beia.solomon.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.beia.solomon.R;
import com.beia.solomon.managers.StatsLinearLayoutManager;
import com.beia.solomon.adapters.StatsRecyclerViewAdapter;
import com.beia.solomon.networkPackets.Mall;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    //UI
    public static RecyclerView statsRecyclerview;
    public static StatsRecyclerViewAdapter statsRecyclerViewAdapter;
    public static StatsLinearLayoutManager statsLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initUI() {
        statsRecyclerview = findViewById(R.id.statsRecyclerView);
        ArrayList<Mall> malls = MainActivity.malls;
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for(Mall mall : malls)
            bitmaps.add(Bitmap.createBitmap(BitmapFactory.decodeByteArray(mall.getImage(), 0, mall.getImage().length)));
        statsRecyclerViewAdapter = new StatsRecyclerViewAdapter(getApplicationContext(), malls, bitmaps);
        statsRecyclerview.setAdapter(statsRecyclerViewAdapter);
        statsLinearLayoutManager = new StatsLinearLayoutManager(getApplicationContext());
        statsLinearLayoutManager.setScrollEnabled(true);
        statsRecyclerview.setLayoutManager(statsLinearLayoutManager);
    }
}
