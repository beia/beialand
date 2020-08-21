package com.beia.solomon.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon.R;
import com.beia.solomon.activities.MallStatsActivity;
import com.beia.solomon.model.Mall;

import java.util.List;

public class StatsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Mall> malls;

    public StatsRecyclerViewAdapter(Context context, List<Mall> malls) {
        this.context = context;
        this.malls = malls;
    }

    public static class MallViewHolder extends RecyclerView.ViewHolder {
        //MALL
        TextView mallName;
        ImageView mallImage;

        MallViewHolder(View itemView) {
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
        //TODO: load mall image from url
        mallViewHolder.mallName.setOnClickListener(view -> {
            Intent intent = new Intent(context, MallStatsActivity.class);
            intent.putExtra("mall", malls.get(position));
            context.startActivity(intent);
        });
        mallViewHolder.mallImage.setOnClickListener(view -> {
            Intent intent = new Intent(context, MallStatsActivity.class);
            intent.putExtra("mall", malls.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return malls.size();
    }
}
