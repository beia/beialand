package com.beia.solomon.networkPackets;

import data.ColorRGB;
import java.io.Serializable;
import java.util.ArrayList;

public class Store implements Serializable
{
    private int id;
    private String name;
    private int mallId;
    private Beacon entranceBeacon;//we will have a beacon at the entrance of the store
    private ArrayList<Beacon> storeBeacons;
    public Store(int id, String name, int mallId, Beacon entranceBeacon, ArrayList<Beacon> storeBeacons)
    {
        this.id = id;
        this.name = name;
        this.mallId = mallId;
        this.entranceBeacon = entranceBeacon;
        this.storeBeacons = storeBeacons;
    }
    public int getId()
    {
        return this.id;
    }
    public String getName()
    {
        return this.name;
    }
    public int getMallId()
    {
        return this.mallId;
    }
    public Beacon getEntranceBeacon()
    {
        return this.entranceBeacon;
    }
    public ArrayList<Beacon> getStoreBeacons()
    {
        return this.storeBeacons;
    }
}
