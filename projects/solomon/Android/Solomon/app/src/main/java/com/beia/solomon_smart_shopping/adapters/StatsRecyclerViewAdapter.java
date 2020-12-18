package com.beia.solomon_smart_shopping.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.model.Mall;
import com.beia.solomon_smart_shopping.model.Status;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.List;

public class StatsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Mall> malls;

    public StatsRecyclerViewAdapter(Context context, List<Mall> malls) {
        this.context = context;
        this.malls = malls;
    }

    public static class MallViewHolder extends RecyclerView.ViewHolder {
        TextView mallName;
        ImageView mallImage;
        TextView parkingSpacesText;
        ProgressBar parkingSpacesProgressBar;

        MallViewHolder(View itemView) {
            super(itemView);
            mallName = itemView.findViewById(R.id.mallName);
            mallImage = itemView.findViewById(R.id.mallImage);
            parkingSpacesText = itemView.findViewById(R.id.freeParkingSpacesText);
            parkingSpacesProgressBar = itemView.findViewById(R.id.parkingSpacesProgressBar);
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mall_stats, parent, false);
        return new MallViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Mall mall = malls.get(position);
        MallViewHolder mallViewHolder = (MallViewHolder) holder;
        mallViewHolder.mallName.setText(malls.get(position).getName());
        GlideUrl glideUrl = new GlideUrl(context.getResources().getString(R.string.mallPicturesUrl) + "/" + mall.getId() + ".png",
                new LazyHeaders.Builder()
                        .addHeader("Authorization", context.getResources().getString(R.string.universal_user))
                        .build());
        Glide.with(context)
                .asBitmap()
                .load(glideUrl)
                .into(mallViewHolder.mallImage);
        if(mall.getParkingSpaces() != null) {
            long freeParkingSpaces = mall.getParkingSpaces()
                    .stream()
                    .filter(parkingSpace -> parkingSpace.getParkingData() != null
                            && parkingSpace
                            .getParkingData()
                            .get(parkingSpace.getParkingData().size() - 1)
                            .getStatus().equals(Status.FREE))
                    .count();
            int freeParkingSpacesPercentage = (int)(((double)freeParkingSpaces / mall.getParkingSpaces().size()) * 100);
            mallViewHolder.parkingSpacesText.setText(String.format("Free parking spaces: %d", freeParkingSpaces));
            mallViewHolder.parkingSpacesProgressBar.setProgress(freeParkingSpacesPercentage);
        }
    }

    @Override
    public int getItemCount() {
        if(malls != null)
            return malls.size();
        return 0;
    }
}
