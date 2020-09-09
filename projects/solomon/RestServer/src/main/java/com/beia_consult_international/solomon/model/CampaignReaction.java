package com.beia_consult_international.solomon.model;

import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
public class CampaignReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(nullable = false)
    private LocalDateTime date;

    public CampaignReaction() {
    }

    public CampaignReaction(long id, User user, Campaign campaign, LocalDateTime date) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
