package com.main.citisim.data;

import java.lang.reflect.Array;
import java.nio.channels.FileLock;
import java.util.ArrayList;

public class DeviceParameters
{
    public String usecase;
    private String time;//1
    private float latitude;//2
    private float longitude;//3
    private float humidity;//4
    private float speed;//5
    private float cO2;//6
    private float temperature;//7
    private float dust;//8
    private float airQuality;//12
    public DeviceParameters(float latitude, float longitude, float cO2, float dust, float airQuality, float speed)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cO2 = cO2;
        this.dust = dust;
        this.airQuality = airQuality;
        this.speed = speed;
    }
    public float getLatitude()
    {
        return this.latitude;
    }
    public float getLongitude()
    {
        return this.longitude;
    }
    public float getCO2()
    {
        return this.cO2;
    }
    public float getDust()
    {
        return this.dust;
    }
    public float getAirQuality()
    {
        return this.airQuality;
    }
    public float getSpeed()
    {
        return this.speed;
    }
    public String getTime() { return this.time; }
    public void setUsecase(String usecase) { this.usecase = usecase;}
    public void setTime(String time) { this.time = time; }
}
