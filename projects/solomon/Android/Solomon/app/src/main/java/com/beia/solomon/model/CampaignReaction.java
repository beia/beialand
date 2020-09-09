package com.beia.solomon.model;

import lombok.Builder;

@Builder
public class CampaignReaction {
    private long id;
    private User user;
    private Campaign campaign;
    private String date;

    public CampaignReaction() {
    }

    public CampaignReaction(long id, User user, Campaign campaign, String date) {
        this.id = id;
        this.user = user;
        this.campaign = campaign;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
