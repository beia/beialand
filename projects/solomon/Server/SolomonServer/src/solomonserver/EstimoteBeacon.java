/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

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
