package com.beia.solomon.networkPackets;

/**
 *
 * @author beia
 */
public class EstimoteBeacon extends Beacon
{
    public static String COMPANY = "Estimote";

    public EstimoteBeacon(String id, String label)
    {
        this.id = id;
        this.label = label;
    }
    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}