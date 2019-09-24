package com.main.citisim;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClusterItem implements ClusterItem  {

    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    /*public MarkerClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }*/

    public MarkerClusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

}
