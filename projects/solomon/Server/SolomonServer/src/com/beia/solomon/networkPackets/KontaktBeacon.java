package com.beia.solomon.networkPackets;

/**
 *
 * @author beia
 */
public class KontaktBeacon extends Beacon
{
    public final String COMPANY = "Kontakt";
    private String major;
    private String minor;
    
    public KontaktBeacon(String id, String label, String major, String minor)
    {
        this.id = id;
        this.label = label;
        this.major = major;
        this.minor = minor;
    }
    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getLabel()
    {
        return this.label;
    }
    
    public String getMajor()
    {
        return this.major;
    }
    
    public String getMinor()
    {
        return this.minor;
    }
}
