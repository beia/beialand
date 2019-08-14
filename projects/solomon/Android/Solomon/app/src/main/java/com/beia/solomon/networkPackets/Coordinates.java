/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beia.solomon.networkPackets;

import java.io.Serializable;

/**
 *
 * @author beia
 */
public class Coordinates implements Serializable
{
    private double latitude;
    private double longitude;
    public Coordinates(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude()
    {
        return this.latitude;
    }
    public double getLongitude()
    {
        return this.longitude;
    }
}
