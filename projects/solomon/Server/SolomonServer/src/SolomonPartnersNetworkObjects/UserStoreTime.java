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
    private int idUser;
    private int idStore;
    private int idMall;
    private long seconds;
    public UserStoreTime(int idUser, int idStore, int idMall, long seconds)
    {
        this.idUser = idUser;
        this.idStore = idStore;
        this.idMall = idMall;
        this.seconds = seconds;
    }
    public int getIdUser()
    {
        return this.idUser;
    }
    public int getIdStore()
    {
        return this.idStore;
    }
    public int getIdMall()
    {
        return this.idMall;
    }
    public long getSeconds()
    {
        return this.seconds;
    }
}
