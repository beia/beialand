package com.beia.solomon.networkPackets;

import java.io.Serializable;
import java.util.HashMap;

public class BeaconsData implements Serializable
{
    private HashMap<String, Beacon> beacons;
    public BeaconsData(HashMap<String, Beacon> beacons)
    {
        this.beacons = beacons;
    }
    public HashMap<String, Beacon> getBeaconsData()
    {
        return this.beacons;
    }
}
