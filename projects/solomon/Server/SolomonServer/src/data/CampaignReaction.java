package data;

import com.beia.solomon.networkPackets.Campaign;

public class CampaignReaction {
    private String idCampaign;
    private Integer idUser;
    String gender;
    private Integer age;
    private String viewDate;
    public CampaignReaction(String idCampaign, Integer idUser, String gender, Integer age, String viewDate) {
        this.idCampaign = idCampaign;
        this.idUser = idUser;
        this.gender = gender;
        this.age = age;
        this.viewDate = viewDate;
    }
    public String getIdCampaign() { return this.idCampaign; }
    public Integer getIdUser() { return this.idUser; }
    public String getGender() { return this.gender; }
    public Integer getAge() { return this.age; }
    public String getViewDate() { return this.viewDate; }
}
