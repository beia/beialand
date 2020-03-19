package com.beia.solomon.viewObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.beia.solomon.R;
import com.beia.solomon.networkPackets.Campaign;

public class CampaignView extends CardView
{
    public Campaign campaign;
    public TextView companyNameTextView;
    public ImageView campaignImageView;
    public TextView campaignTitleTextView;
    public TextView campaignDescriptionTextView;
    public TextView campaignStartDateTextView;
    public TextView campaignEndDateTextView;
    public CampaignView(Context context, AttributeSet attributeSet) { super(context, attributeSet); }
    public CampaignView(@NonNull Context context, Campaign product) {
        super(context);
        this.campaign = product;
        inflate(context, R.layout.campaign_layout, this);
        this.companyNameTextView = findViewById(R.id.companyName);
        this.campaignImageView = findViewById(R.id.campaignImage);
        this.campaignTitleTextView = findViewById(R.id.campaignTitle);
        this.campaignDescriptionTextView = findViewById(R.id.campaignDescription);
        this.campaignStartDateTextView = findViewById(R.id.startDate);
        this.campaignEndDateTextView = findViewById(R.id.endDate);
        populateProductView(campaign);
    }
    public void populateProductView(Campaign campaign)
    {
        companyNameTextView.setText(campaign.getCompanyName());
        campaignTitleTextView.setText(campaign.getTitle());
        campaignDescriptionTextView.setText(campaign.getDescription());
        campaignStartDateTextView.setText(campaign.getStartDate());
        campaignEndDateTextView.setText(campaign.getEndDate());
        Bitmap bitmap = BitmapFactory.decodeByteArray(campaign.getImage(), 0, campaign.getImage().length);
        campaignImageView.setImageBitmap(bitmap);
    }
}
