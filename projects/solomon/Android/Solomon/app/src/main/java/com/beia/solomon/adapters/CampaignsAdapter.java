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
import com.beia.solomon.runnables.RequestRunnable;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CampaignsAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<Campaign> campaigns;
    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
        final Campaign campaign = campaigns.get(position);
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
        Glide.with(context)
                .asBitmap()
                .load(bitmap)
                .into(campaignImageView);

        //load the company image
        if(campaign.getCompanyImage() != null) {
            ImageView companyImageView = campaignView.findViewById(R.id.companyImage);
            Bitmap companyImageBitmap = BitmapFactory.decodeByteArray(campaign.getCompanyImage(), 0, campaign.getCompanyImage().length);
            Glide.with(context).
                    asBitmap().
                    load(companyImageBitmap).
                    into(companyImageView);
        }

        campaignView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.tabLayout.getTabAt(1).select();
                //send the campaigns reaction to the server
                Calendar calendar = Calendar.getInstance();
                String currentTime = dateFormat.format(calendar.getTime());
                String request = "{\"requestType\":\"postCampaignReaction\", \"idCampaign\":\"" + campaign.getId() + "\", \"idUser\":" + MainActivity.userData.getUserId() + ", \"gender\":\"" + MainActivity.userData.getGender() + "\", \"age\":" + MainActivity.userData.getAge() + ", \"viewDate\":\"" + currentTime + "\"}";
                new Thread(new RequestRunnable(request, MainActivity.objectOutputStream)).start();
            }
        });
        return campaignView;
    }
}
