/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SolomonPartnersNetworkObjects;

import java.io.Serializable;

/**
 *
 * @author beia
 */
public class UserStoreTime implements Serializable
{
    private int idMall;
    private int idStore;
    private String storeName;
    private long seconds;
    public UserStoreTime(int idMall, int idStore, String storeName, long seconds)
    {
        this.idStore = idStore;
        this.idMall = idMall;
        this.seconds = seconds;
        this.storeName = storeName;
    }
    public int getIdStore()
    {
        return this.idStore;
    }
    public int getIdMall()
    {
        return this.idMall;
    }
    public String getStoreName()
    {
        return this.storeName;
    }
    public long getSeconds()
    {
        return this.seconds;
    }
}
