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
    public volatile HashMap<String, String> companiesMap;
    public volatile HashMap<String, Campaign> campaignsMapById;
    public volatile HashMap<String, ArrayList<Campaign>> campaignsMapByCompanyName;
    public UpdateCampaignsForUsersRunnable(HashMap<String, String> companiesMap, HashMap<String, Campaign> campaignsMapById, HashMap<String, ArrayList<Campaign>> campaignsMapByCompanyName)
    {
        this.companiesMap = companiesMap;
        this.campaignsMapById = campaignsMapById;
        this.campaignsMapByCompanyName = campaignsMapByCompanyName;
    }
    @Override
    public void run() {
        while(true)
        {
            try
            {
                String currentDate = dateFormat.format(cal.getTime());
                campaignsMapByCompanyName.clear();
                //check for valid campaigns
                for(Campaign campaign : campaignsMapById.values())
                {
                    if(currentDate.compareTo(campaign.getStartDate()) >= 0 && currentDate.compareTo(campaign.getEndDate()) <= 0)
                    {
                        String companyName = companiesMap.get(campaign.getIdCompany());
                        System.out.println(companyName + "valid \n\n\n\n");
                        if(!campaignsMapByCompanyName.containsKey(companyName))
                        {
                            ArrayList<Campaign> storeCampaigns = new ArrayList<>();
                            Campaign campaignForUser = new Campaign(campaign.getId(), campaign.getCompanyName(), campaign.getTitle(), campaign.getDescription(), campaign.getStartDate(), campaign.getEndDate(), getImageFromDisk(campaign.getPhotoPath()));
                            storeCampaigns.add(campaignForUser);
                            campaignsMapByCompanyName.put(companyName, storeCampaigns);
                            System.out.println("Added campaign");
                        }
                        else
                        {
                            campaignsMapByCompanyName.get(companyName).add(new Campaign(campaign.getId(), campaign.getCompanyName(), campaign.getTitle(), campaign.getDescription(), campaign.getStartDate(), campaign.getEndDate(), getImageFromDisk(campaign.getPhotoPath())));
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
