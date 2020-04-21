package com.beia.solomon.networkPackets;

import java.io.Serializable;

public abstract class Beacon implements Serializable
{
    protected String id;
    protected String label;
    protected int mallId;
    protected String companyId;
    protected Coordinates coordinates;
    protected int layer;
    protected int floor;
    protected byte[] image;

    public abstract String getId();
    public abstract String getLabel();
    public abstract int getMallId();
    public abstract String getCompanyId();
    public abstract Coordinates getCoordinates();
    public abstract int getLayer();
    public abstract int getFloor();
    public abstract byte[] getImage();

    public abstract void setImage(byte[] image);
}