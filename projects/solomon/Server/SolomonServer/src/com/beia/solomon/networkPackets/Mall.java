/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beia.solomon.networkPackets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author beia
 */
public class Mall implements Serializable
{
    private int mallId;
    private String mallName;
    private byte[] image;
    private ArrayList<Store> stores;
    private Coordinates mallCoordinates;
    public Mall(int mallId, String mallName, byte[] image, ArrayList<Store> stores, Coordinates mallCoordinates)
    {
        this.mallId = mallId;
        this.mallName = mallName;
        this.image = image;
        this.stores = stores;
        this.mallCoordinates = mallCoordinates;
    }
    public int getMallId()
    {
        return this.mallId;
    }
    public String getName() { return this.mallName; }
    public byte[] getImage() { return this.image; }
    public ArrayList<Store> getStores()
    {
        return this.stores;
    }
    public Coordinates getMallCoordinates()
    {
        return this.mallCoordinates;
    }
}