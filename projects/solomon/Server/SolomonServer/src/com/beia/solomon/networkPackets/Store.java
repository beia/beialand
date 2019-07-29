package com.beia.solomon.networkPackets;

import data.ColorRGB;

public class Store
{
    private int id;
    private String name;
    private ColorRGB contourColor;
    public Store(int id, String name, ColorRGB contourColor)
    {
        this.id = id;
        this.name = name;
        this.contourColor = contourColor;
    }
    public int getId()
    {
        return this.id;
    }
    public String getName()
    {
        return this.name;
    }
}
