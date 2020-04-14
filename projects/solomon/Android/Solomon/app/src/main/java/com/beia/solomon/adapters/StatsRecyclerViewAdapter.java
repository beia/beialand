package com.beia.solomon.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.activities.MallStatsActivity;
import com.beia.solomon.activities.StatsActivity;
import com.beia.solomon.networkPackets.Mall;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StatsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Mall> malls;
    private ArrayList<Bitmap> bitmaps;

    public StatsRecyclerViewAdapter(Context context, ArrayList<Mall> malls, ArrayList<Bitmap> bitmaps) {
        this.context = context;
        this.malls = malls;
        this.bitmaps = bitmaps;
    }

    public static class MallViewHolder extends RecyclerView.ViewHolder {
        //MALL
        TextView mallName;
        ImageView mallImage;

        public MallViewHolder(View itemView) {
            super(itemView);
            mallName = itemView.findViewById(R.id.mallName);
            mallImage = itemView.findViewById(R.id.mallImage);
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mall_stats, parent, false);
        return new MallViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MallViewHolder mallViewHolder = (MallViewHolder) holder;
        mallViewHolder.mallName.setText(malls.get(position).getName());
        Glide.with(context)
                .asBitmap()
                .load(bitmaps.get(position))
                .into(mallViewHolder.mallImage);

        //set click listeners
        mallViewHolder.mallName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MallStatsActivity.class);
                intent.putExtra("mallId", malls.get(position).getMallId());
                context.startActivity(intent);
            }
        });
        mallViewHolder.mallImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MallStatsActivity.class);
                intent.putExtra("mallId", malls.get(position).getMallId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return malls.size();
    }
}
