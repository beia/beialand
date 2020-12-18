package com.beia.solomon_smart_shopping.adapters;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon_smart_shopping.GsonRequest;
import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.model.Campaign;
import com.beia.solomon_smart_shopping.model.CampaignReaction;
import com.beia.solomon_smart_shopping.model.User;
import com.bumptech.glide.Glide;

import org.threeten.bp.LocalDateTime;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampaignsAdapter extends BaseAdapter {

    private RequestQueue volleyQueue;
    private Context context;
    private List<Campaign> campaigns;
    private User user;

    private void postCampaignReaction(CampaignReaction campaignReaction) {
        String url = context.getResources().getString(R.string.postCampaignReactionUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", context.getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                campaignReaction,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "SAVED CAMPAIGN REACTION");
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "postCampaignReaction: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public CampaignsAdapter(Context context, List<Campaign> campaigns, User user) {
        this.context = context;
        this.campaigns = campaigns;
        this.user = user;
        volleyQueue = Volley.newRequestQueue(context);
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
            byte[] companyImage = Base64.getMimeDecoder().decode(campaign.getUser().getImage());
            Bitmap companyImageBitmap = BitmapFactory.decodeByteArray(companyImage, 0, companyImage.length);
            Glide.with(context).
                    asBitmap().
                    load(companyImageBitmap).
                    into(companyImageView);
        }

        campaignView.setOnClickListener((v) ->
            postCampaignReaction(CampaignReaction
                    .builder()
                    .user(user)
                    .campaign(campaign)
                    .date(LocalDateTime.now().toString())
                    .build()));

        return campaignView;
    }
}
