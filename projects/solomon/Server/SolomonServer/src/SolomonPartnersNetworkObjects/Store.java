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
public class Store implements Serializable
{
    private int idMall;
    private String name;
    public Store(int idMall, String name)
    {
        this.idMall = idMall;
        this.name = name;
    }
    public int getIdMall()
    {
        return this.idMall;
    }
    public String getName()
    {
        return this.name;
    }
}
