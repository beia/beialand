package com.example.solomon.networkPackets;

public class Store
{
    private String id;
    private String name;
    private byte[] mapImageBytes;
    public Store(String id, String name, byte[] mapImageBytes)
    {
        this.id = id;
        this.name = name;
        this.mapImageBytes = mapImageBytes;
    }
    public String getId()
    {
        return this.id;
    }
    public String getName()
    {
        return this.name;
    }
    public byte[] getMapImageBytes()
    {
        return this.getMapImageBytes();
    }
}
