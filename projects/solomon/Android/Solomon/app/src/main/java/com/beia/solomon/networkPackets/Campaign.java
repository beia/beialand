package com.beia.solomon.networkPackets;
public class Campaign
{
    private String idCampaign;
    private String companyName;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private byte[] image;
    public Campaign(String idCampaign, String companyName, String title, String description, String startDate, String endDate, byte[] image)
    {
        this.idCampaign = idCampaign;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
    }
    public Campaign(String idCampaign, String companyName, String title, String description, String startDate, String endDate)
    {
        this.idCampaign = idCampaign;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public String getId() { return this.idCampaign; }
    public String getCompanyName() { return this.companyName; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public byte[] getImage() { return this.image; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setImage(byte[] image) { this.image = image; }
}

