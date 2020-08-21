package com.beia.solomon.activities;

import android.os.Bundle;
import android.renderscript.Type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon.R;
import com.beia.solomon.adapters.StatsRecyclerViewAdapter;
import com.beia.solomon.managers.StatsLinearLayoutManager;
import com.beia.solomon.model.Mall;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    public List<Mall> malls;
    public RecyclerView statsRecyclerview;
    public StatsRecyclerViewAdapter statsRecyclerViewAdapter;
    public StatsLinearLayoutManager statsLinearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        String mallsJson = getIntent().getStringExtra("malls");
        Type type = (Type) new TypeToken<List<Mall>>(){}.getType();
        malls = new Gson().fromJson(mallsJson, (java.lang.reflect.Type) type);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initUI() {
        statsRecyclerview = findViewById(R.id.statsRecyclerView);
        statsRecyclerViewAdapter = new StatsRecyclerViewAdapter(getApplicationContext(), malls);
        statsRecyclerview.setAdapter(statsRecyclerViewAdapter);
        statsLinearLayoutManager = new StatsLinearLayoutManager(getApplicationContext());
        statsLinearLayoutManager.setScrollEnabled(true);
        statsRecyclerview.setLayoutManager(statsLinearLayoutManager);
    }
}
