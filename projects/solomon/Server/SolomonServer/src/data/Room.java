/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.Coordinates;

/**
 *
 * @author beia
 */
public class Room extends Beacon
{
    private long timeSeconds;
    public Room(String id, String label, int mallId, long timeSeconds) {
        this.id = id;
        this.label = label;
        this.mallId = mallId;
        this.timeSeconds = timeSeconds;
    }
    public Room(String label, long timeSeconds) {
        this.label = label;
        this.timeSeconds = timeSeconds;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public int getMallId()
    {
        return this.mallId;
    }
    
    @Override
    public Coordinates getCoordinates()
    {
        return this.coordinates;
    }
    
    @Override
    public int getLayer()
    {
        return this.layer;
    }
    
    @Override
    public int getFloor()
    {
        return this.floor;
    }
    
     public long getTimeSeconds()
    {
        return this.timeSeconds;
    }
}
