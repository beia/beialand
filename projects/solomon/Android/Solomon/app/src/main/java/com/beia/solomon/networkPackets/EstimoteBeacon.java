package com.beia.solomon.networkPackets;

/**
 *
 * @author beia
 */
public class EstimoteBeacon extends Beacon
{
    public static String MANUFACTURER = "Estimote";

    public EstimoteBeacon(String id, String label, int mallId, String companyId, Coordinates coordinates, int layer, int floor)
    {
        this.id = id;
        this.label = label;
        this.mallId = mallId;
        this.companyId = companyId;
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
    public String getCompanyId() { return this.companyId; }

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

    @Override
    public byte[] getImage() { return this.image; }

    @Override
    public void setImage(byte[] image) { this.image = image;}
}
