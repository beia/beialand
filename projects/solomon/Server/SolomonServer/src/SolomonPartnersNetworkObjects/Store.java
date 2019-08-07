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
public class Store implements Serializable
{
    private int idStore;
    private int idMall;
    private String name;
    private ArrayList<String> categories;
    private ArrayList<SpecialOffer> specialOffers;
    public Store(int idStore, int idMall, String name, ArrayList<String> categories, ArrayList<SpecialOffer> specialOffers)
    {
        this.idStore = idStore;
        this.idMall = idMall;
        this.name = name;
        this.categories = categories;
        this.specialOffers = specialOffers;
    }
    public int getIdStore()
    {
        return this.idStore;
    }
    public int getIdMall()
    {
        return this.idMall;
    }
    public String getName()
    {
        return this.name;
    }
    public ArrayList<String> getCategories()
    {
        return this.categories;
    }
    public ArrayList<SpecialOffer> getSpecialOffers()
    {
        return this.specialOffers;
    }
}
