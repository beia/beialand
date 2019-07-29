/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beia.solomon.networkPackets;
import data.ColorRGB;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author beia
 */
public class MallData
{
    private int idMall;
    private String name;
    private ColorRGB[][] mapImageRGB;
    private ArrayList<Store> stores;
    private HashMap<String, Store> storeContours;
    public MallData(int idMall, String name, ColorRGB[][] mapImageRGB, ArrayList<Store> stores, HashMap<String,Store> storesContours)
    {
        this.idMall = idMall;
        this.name = name;
        this.mapImageRGB = mapImageRGB;
        this.stores = stores;
        this.storeContours = storeContours;
    }
    public int getIdMall()
    {
        return this.idMall;
    }
    public String getName()
    {
        return this.name;
    }
    public ColorRGB[][] getImageRGB()
    {
        return this.mapImageRGB;
    }
    public ArrayList<Store> getStores()
    {
        return this.stores;
    }
    public HashMap<String, Store> getStoreContours()
    {
        return this.storeContours;
    }
}