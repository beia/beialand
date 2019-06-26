package com.example.solomon.networkPackets;

import java.io.Serializable;

public class ImageData implements Serializable
{
    private byte[] imageBytes;
    private int userId;
    public ImageData(byte[] imageBytes, int userId)
    {
        this.imageBytes = imageBytes;
        this.userId = userId;
    }
    public byte[] getImageBytes()
    {
        return imageBytes;
    }
    public int getUserId()
    {
        return this.userId;
    }
}
