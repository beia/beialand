package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class LocationData implements Serializable
{
    private int userId;
    private String beaconId;
    private String beaconLabel;
    private int mallId;
    private boolean zoneEntered;
    private String time;
    public LocationData(int userId,String beaconId, String beaconLabel, int mallId, boolean zoneEntered, String time)
    {
        this.userId = userId;
        this.beaconId = beaconId;
        this.beaconLabel = beaconLabel;
        this.mallId = mallId;
        this.zoneEntered = zoneEntered;
        this.time = time;
    }
    public int getUserId()
    {
        return this.userId;
    }
    public String getBeaconId()
    {
        return this.beaconId;
    }
    public String getBeaconLabel()
    {
        return this.beaconLabel;
    }
    public int getMallId()
    {
        return this.mallId;
    }
    public boolean getZoneEntered()
    {
        return this.zoneEntered;
    }
    public String getTime()
    {
        return this.time;
    }
}
