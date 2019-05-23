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
public class Room extends Beacon
{
    private long timeSeconds;
    public Room(String id, String label, String name, long timeSeconds) {
        super(id, label, name);
        this.timeSeconds = timeSeconds;
    }
    public Room(String name, long timeSeconds) {
        super(name);
        this.timeSeconds = timeSeconds;
    }
    public long getTimeSeconds()
    {
        return this.timeSeconds;
    }
}
