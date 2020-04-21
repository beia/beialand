package runnables;

import com.beia.solomon.networkPackets.Campaign;
import com.mysql.cj.x.protobuf.MysqlxCrud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateCampaignsForUsersRunnable implements Runnable
{
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    public volatile HashMap<String, String> companiesMap;//key:id value:companyName
    public volatile HashMap<String, Campaign> campaignsMapById;
    public volatile HashMap<String, ArrayList<Campaign>> campaignsMapByCompanyId;
    public UpdateCampaignsForUsersRunnable(HashMap<String, String> companiesMap, HashMap<String, Campaign> campaignsMapById, HashMap<String, ArrayList<Campaign>> campaignsMapByCompanyId)
    {
        this.companiesMap = companiesMap;
        this.campaignsMapById = campaignsMapById;
        this.campaignsMapByCompanyId = campaignsMapByCompanyId;
    }
    @Override
    public void run() {
        while(true)
        {
            try
            {
                String currentDate = dateFormat.format(cal.getTime());
                campaignsMapByCompanyId.clear();
                //check for valid campaigns
                for(Campaign campaign : campaignsMapById.values())
                {
                    if(currentDate.compareTo(campaign.getStartDate()) >= 0 && currentDate.compareTo(campaign.getEndDate()) <= 0)
                    {
                        if(!campaignsMapByCompanyId.containsKey(campaign.getIdCompany()))
                        {
                            ArrayList<Campaign> storeCampaigns = new ArrayList<>();
                            Campaign campaignForUser = new Campaign(campaign.getId(), campaign.getIdCompany(), companiesMap.get(campaign.getIdCompany()), campaign.getTitle(), campaign.getDescription(), campaign.getStartDate(), campaign.getEndDate(), getImageFromDisk(campaign.getPhotoPath()));
                            storeCampaigns.add(campaignForUser);
                            campaignsMapByCompanyId.put(campaign.getIdCompany(), storeCampaigns);
                        }
                        else
                        {
                            campaignsMapByCompanyId.get(campaign.getIdCompany()).add(new Campaign(campaign.getId(), campaign.getIdCompany(), companiesMap.get(campaign.getIdCompany()), campaign.getTitle(), campaign.getDescription(), campaign.getStartDate(), campaign.getEndDate(), getImageFromDisk(campaign.getPhotoPath())));
                        }
                    }
                }
                Thread.sleep(10 * 60 * 1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static byte[] getImageFromDisk(String path)
    {
        File file = new File(path);
        byte[] imageBytes = new byte[(int) file.length()];
        try
        {
            //read file into bytes[]
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(imageBytes);
            fileInputStream.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return imageBytes;
    }
}
