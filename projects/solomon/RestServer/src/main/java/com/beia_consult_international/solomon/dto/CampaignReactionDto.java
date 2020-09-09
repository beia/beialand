package com.beia_consult_international.solomon.dto;

import lombok.Builder;

@Builder
public class CampaignReactionDto {
    private long id;
    private UserDto user;
    private CampaignDto campaign;
    private String date;

    public CampaignReactionDto() {
    }

    public CampaignReactionDto(long id, UserDto user, CampaignDto campaign, String date) {
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public CampaignDto getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignDto campaign) {
        this.campaign = campaign;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
