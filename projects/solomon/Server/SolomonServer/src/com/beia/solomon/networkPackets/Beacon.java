package com.beia.solomon.networkPackets;

import java.io.Serializable;

public abstract class Beacon implements Serializable
{
    protected String id;
    protected String label;
    
    public abstract String getId();
    public abstract String getLabel();
}