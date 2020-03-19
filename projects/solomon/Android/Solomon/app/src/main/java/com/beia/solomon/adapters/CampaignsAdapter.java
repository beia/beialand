package com.beia.solomon.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.beia.solomon.networkPackets.Campaign;
import com.beia.solomon.viewObjects.CampaignView;

import java.util.ArrayList;

public class CampaignsAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<Campaign> campaigns;
    public CampaignsAdapter(Context context, ArrayList<Campaign> campaigns)
    {
        this.context = context;
        this.campaigns = campaigns;
    }

    @Override
    public int getCount() {
        return campaigns.size();
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
        CampaignView campaignView = new CampaignView(context, campaigns.get(position));
        return campaignView;
    }
}
