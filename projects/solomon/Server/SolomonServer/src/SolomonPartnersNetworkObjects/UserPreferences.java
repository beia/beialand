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
public class UserPreferences implements Serializable
{
    private int idUser;
    private String category;
    public UserPreferences(int idUser, String category)
    {
        this.idUser = idUser;
        this.category = category;
    }
    public int getIdUser()
    {
        return this.idUser;
    }
    public String getCategory()
    {
        return this.category;
    }
}
