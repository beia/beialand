/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.solomon.networkPackets;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author beia
 */
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
