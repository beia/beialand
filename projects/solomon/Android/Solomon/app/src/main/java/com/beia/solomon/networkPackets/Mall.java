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
    private ArrayList<Store> stores;
    private Coordinates mallCoordinates;
    public Mall(int mallId, ArrayList<Store> stores, Coordinates mallCoordinates)
    {
        this.mallId = mallId;
        this.stores = stores;
        this.mallCoordinates = mallCoordinates;
    }
    public int getMallId()
    {
        return this.mallId;
    }
    public ArrayList<Store> getStores()
    {
        return this.stores;
    }
    public Coordinates getMallCoordinates()
    {
        return this.mallCoordinates;
    }
}