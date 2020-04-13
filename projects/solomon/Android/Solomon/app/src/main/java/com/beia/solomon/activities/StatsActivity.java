package com.beia.solomon.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.beia.solomon.R;
import com.beia.solomon.adapters.MallStatsAdapter;
import com.beia.solomon.runnables.RequestRunnable;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    //UI
    public static ListView mallsListView;
    public static MallStatsAdapter mallStatsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initUI();
    }

    public void initUI() {
        mallsListView = findViewById(R.id.mallsListView);
        mallStatsAdapter = new MallStatsAdapter(getApplicationContext(), MainActivity.malls);
        mallsListView.setAdapter(mallStatsAdapter);
    }
}
