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
public class SpecialOffer implements Serializable
{
    private int idStore;
    private String description;
    private ArrayList<String> categories;
    public SpecialOffer(int idStore, String description, ArrayList<String> categories)
    {
        this.idStore = idStore;
        this.description = description;
        this.categories = categories;
    }
    public int getIdStore()
    {
        return this.idStore;
    }
    public String getDescription()
    {
        return this.description;
    }
    public ArrayList<String> getCategories()
    {
        return this.categories;
    }
}