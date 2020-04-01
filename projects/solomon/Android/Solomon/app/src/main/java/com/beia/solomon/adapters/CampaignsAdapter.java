package com.beia.solomon.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.R;
import com.beia.solomon.networkPackets.Campaign;

import java.util.ArrayList;

public class CampaignsAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<Campaign> campaigns;
    public CampaignsAdapter(Context context, ArrayList<Campaign> campaigns)
    {
        this.context = context;
        this.campaigns = campaigns;
        Log.d("CAMPAIGN", "CampaignsAdapter: " + campaigns.size());
    }

    @Override
    public int getCount() {
        if(campaigns != null)
            return campaigns.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return campaigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Campaign campaign = campaigns.get(position);
        View campaignView = LayoutInflater.from(context).inflate(R.layout.campaign_layout, parent, false);
        TextView companyNameTextView = campaignView.findViewById(R.id.companyName);
        ImageView campaignImageView = campaignView.findViewById(R.id.campaignImage);
        TextView campaignTitleTextView = campaignView.findViewById(R.id.campaignTitle);
        TextView campaignDescriptionTextView = campaignView.findViewById(R.id.campaignDescription);
        TextView campaignStartDateTextView = campaignView.findViewById(R.id.startDate);
        TextView campaignEndDateTextView = campaignView.findViewById(R.id.endDate);
        companyNameTextView.setText(campaign.getCompanyName());
        campaignTitleTextView.setText(campaign.getTitle());
        campaignDescriptionTextView.setText(campaign.getDescription());
        campaignStartDateTextView.setText(campaign.getStartDate());
        campaignEndDateTextView.setText(campaign.getEndDate());
        Bitmap bitmap = BitmapFactory.decodeByteArray(campaign.getImage(), 0, campaign.getImage().length);
        campaignImageView.setImageBitmap(bitmap);

        campaignView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.tabLayout.getTabAt(1).select();
            }
        });
        return campaignView;
    }
}
