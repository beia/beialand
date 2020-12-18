package com.beia.solomon_smart_shopping.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.adapters.StatsRecyclerViewAdapter;
import com.beia.solomon_smart_shopping.managers.StatsLinearLayoutManager;
import com.beia.solomon_smart_shopping.model.Mall;
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
        setTheme(R.style.AppTheme);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        String mallsJson = getIntent().getStringExtra("malls");
        malls = new Gson().fromJson(mallsJson, new TypeToken<List<Mall>>(){}.getType());
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
