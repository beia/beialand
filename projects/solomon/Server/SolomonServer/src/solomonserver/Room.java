/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import com.example.solomon.networkPackets.Beacon;

/**
 *
 * @author beia
 */
public class Room extends Beacon
{
    private long timeSeconds;
    public Room(String id, String label, long timeSeconds) {
        this.id = id;
        this.label = label;
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
    
     public long getTimeSeconds()
    {
        return this.timeSeconds;
    }
}
