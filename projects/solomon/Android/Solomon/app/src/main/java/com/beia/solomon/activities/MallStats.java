package com.beia.solomon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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

public class MallStats extends AppCompatActivity {
    //MALL STATS VARIABLES
    public static volatile int parkingSpacesAvailablePercentage;
    public int mallId;
    //UI VARIABLES
    public static PieChart parkingStatsPieChart;

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://PARKING STATS
                    parkingSpacesAvailablePercentage = (Integer) msg.obj;
                    //update the chart
                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    pieEntries.add(new PieEntry(parkingSpacesAvailablePercentage, "free parking spaces"));
                    pieEntries.add(new PieEntry(100 - parkingSpacesAvailablePercentage, "occupied parking spaces"));

                    PieDataSet dataSet = new PieDataSet(pieEntries, "");
                    dataSet.setDrawIcons(false);
                    dataSet.setSliceSpace(4f);
                    dataSet.setIconsOffset(new MPPointF(0, 40));
                    dataSet.setSelectionShift(5f);
                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setValueLinePart1OffsetPercentage(60.f);
                    dataSet.setValueLinePart1Length(0.5f);//line vertical length
                    dataSet.setValueLinePart2Length(.08f);//line horizontal length
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return (int)value + "%";
                        }
                    });

                    //colors
                    ArrayList<Integer> colors = new ArrayList<>();
                    colors.add(MainActivity.context.getResources().getColor(R.color.greenAccent));
                    colors.add(MainActivity.context.getResources().getColor(R.color.red));
                    dataSet.setColors(colors);

                    PieData data = new PieData(dataSet);
                    data.setValueTextSize(MainActivity.context.getResources().getDimension(R.dimen.dimen2_8sp));
                    data.setValueTextColor(MainActivity.context.getResources().getColor(R.color.darkgray));

                    //data.setValueTypeface(tfLight);
                    parkingStatsPieChart.setData(data);

                    Legend legend = parkingStatsPieChart.getLegend();
                    legend.setTextSize(13);
                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);

                    parkingStatsPieChart.setDrawEntryLabels(false);
                    parkingStatsPieChart.getDescription().setEnabled(false);
                    parkingStatsPieChart.setDragDecelerationFrictionCoef(0.95f);
                    parkingStatsPieChart.setHighlightPerTapEnabled(true);
                    parkingStatsPieChart.setExtraOffsets(0, 8, 50, 8);
                    parkingStatsPieChart.offsetLeftAndRight(0);
                    parkingStatsPieChart.getCircleBox().offset(0, 0);
                    parkingStatsPieChart.spin(0, parkingStatsPieChart.getRotationAngle(), parkingStatsPieChart.getRotationAngle() + 10, Easing.EaseInOutQuad);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_stats);

        initUI();

        //PARKING STATS
        //get the mall id and request the parking stats for the mall
        mallId = getIntent().getIntExtra("mallId", -1);
        if(mallId != -1) {
            String request = "{\"requestType\":\"getParkingStats\",\"mallId\":" + mallId + "}";
            new Thread(new RequestRunnable(request, MainActivity.objectOutputStream)).start();
        }
    }

    public void initUI() {
        parkingStatsPieChart = findViewById(R.id.parkingPieChart);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
