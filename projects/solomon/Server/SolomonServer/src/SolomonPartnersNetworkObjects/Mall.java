/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SolomonPartnersNetworkObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author beia
 */
public class Mall implements Serializable
{
    private int mallId;
    private String name;
    private ArrayList<Store> stores;
    public Mall(int mallId, String name, ArrayList<Store> stores)
    {
        this.mallId = mallId;
        this.name = name;
        this.stores = stores;
    }
    public int getMallId()
    {
        return this.mallId;
    }
    public String getName()
    {
        return this.name;
    }
    public ArrayList<Store> getStores()
    {
        return this.stores;
    }
}
