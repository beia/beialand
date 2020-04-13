package com.beia.solomon.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beia.solomon.R;
import com.beia.solomon.activities.MallStats;
import com.beia.solomon.networkPackets.Mall;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

public class MallStatsAdapter extends BaseAdapter
{
    public Context context;
    public ArrayList<Mall> malls;
    public MallStatsAdapter(Context context, ArrayList<Mall> malls) {
        this.context = context;
        this.malls = malls;
        Log.d("MALLS", "MallStatsAdapter: " + malls.size());
    }
    @Override
    public int getCount() {
        if(malls != null)
            return malls.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return malls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return malls.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.mall_stats, viewGroup, false);
            final Mall mall = malls.get(i);
            TextView mallNameTextView = view.findViewById(R.id.mallName);
            ImageView mallImage = view.findViewById(R.id.mallImage);

            //set mall name
            mallNameTextView.setText(mall.getName());
            //set mall picture
            Bitmap mallImageBitmap = Bitmap.createBitmap(BitmapFactory.decodeByteArray(mall.getImage(), 0, mall.getImage().length));
            Glide.with(context)
                    .asBitmap()
                    .load(mallImageBitmap)
                    .into(mallImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MallStats.class);
                    intent.putExtra("mallId", mall.getMallId());
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }
}
