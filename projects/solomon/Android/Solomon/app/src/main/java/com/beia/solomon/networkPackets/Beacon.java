package com.beia.solomon.networkPackets;

import java.io.Serializable;

public abstract class Beacon implements Serializable
{
    protected String id;
    protected String label;
    protected int mallId;

    public abstract String getId();
    public abstract String getLabel();
    public abstract int getMallId();
}