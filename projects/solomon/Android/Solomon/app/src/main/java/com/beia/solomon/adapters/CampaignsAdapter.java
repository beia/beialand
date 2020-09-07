package com.beia.solomon.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beia.solomon.R;
import com.beia.solomon.model.Campaign;
import com.bumptech.glide.Glide;

import java.util.Base64;
import java.util.List;

public class CampaignsAdapter extends BaseAdapter {

    private Context context;
    private List<Campaign> campaigns;

    public CampaignsAdapter(Context context, List<Campaign> campaigns) {
        this.context = context;
        this.campaigns = campaigns;
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
        final Campaign campaign = campaigns.get(position);
        View campaignView = LayoutInflater.from(context).inflate(R.layout.campaign_layout, parent, false);
        TextView companyNameTextView = campaignView.findViewById(R.id.companyName);
        ImageView campaignImageView = campaignView.findViewById(R.id.campaignImage);
        TextView campaignTitleTextView = campaignView.findViewById(R.id.campaignTitle);
        TextView campaignDescriptionTextView = campaignView.findViewById(R.id.campaignDescription);
        TextView campaignStartDateTextView = campaignView.findViewById(R.id.startDate);
        TextView campaignEndDateTextView = campaignView.findViewById(R.id.endDate);
        companyNameTextView.setText(campaign.getUser().getFirstName());
        campaignTitleTextView.setText(campaign.getTitle());
        campaignDescriptionTextView.setText(campaign.getDescription());
        campaignStartDateTextView.setText(campaign.getStartDate());
        campaignEndDateTextView.setText(campaign.getEndDate());

        byte[] campaignImage = Base64.getDecoder().decode(campaign.getImage());
        Bitmap bitmap = BitmapFactory.decodeByteArray(campaignImage, 0, campaignImage.length);
        Glide.with(context)
                .asBitmap()
                .load(bitmap)
                .into(campaignImageView);

        if(campaign.getUser().getImage() != null) {
            ImageView companyImageView = campaignView.findViewById(R.id.companyImage);
            byte[] companyImage = Base64.getDecoder().decode(campaign.getUser().getImage());
            Bitmap companyImageBitmap = BitmapFactory.decodeByteArray(companyImage, 0, companyImage.length);
            Glide.with(context).
                    asBitmap().
                    load(companyImageBitmap).
                    into(companyImageView);
        }
        return campaignView;
    }
}
