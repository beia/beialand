/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonWebClientsObjects;

/**
 *
 * @author beia
 */
public class Campaign 
{
    private String idCampaign;
    private String idCompany;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String photoPath;
    public Campaign(String idCampaign, String idCompany, String title, String description, String startDate, String endDate, String photoPath)
    {
        this.idCampaign = idCampaign;
        this.idCompany = idCompany;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.photoPath = photoPath;
    }
    public String getId() { return this.idCampaign; }
    public String getCompanyId() { return this.idCompany; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public String getPhotoPath() { return this.photoPath; }
}
