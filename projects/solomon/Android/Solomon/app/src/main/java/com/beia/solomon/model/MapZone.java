package com.beia.solomon.model;

import com.google.android.gms.maps.model.LatLng;

public enum MapZone {
    BUCHAREST(new LatLng(44.430591, 26.102977), 11f);

    MapZone(LatLng coordinates, float zoom) {
        this.coordinates = coordinates;
        this.zoom = zoom;
    }
    private LatLng coordinates;
    private float zoom;

    public LatLng getCoordinates() {
        return coordinates;
    }

    public float getZoom() {
        return zoom;
    }
}
