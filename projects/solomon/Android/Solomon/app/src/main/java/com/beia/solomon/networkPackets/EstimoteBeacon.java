package com.beia.solomon.networkPackets;

/**
 *
 * @author beia
 */
public class EstimoteBeacon extends Beacon
{
    public static String COMPANY = "Estimote";

    public EstimoteBeacon(String id, String label, int mallId, Coordinates coordinates, int layer, int floor)
    {
        this.id = id;
        this.label = label;
        this.mallId = mallId;
        this.coordinates = coordinates;
        this.layer = layer;
        this.floor = floor;
    }
    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public int getMallId()
    {
        return this.mallId;
    }

    @Override
    public Coordinates getCoordinates()
    {
        return this.coordinates;
    }

    @Override
    public int getLayer()
    {
        return this.layer;
    }

    @Override
    public int getFloor()
    {
        return this.floor;
    }
}
