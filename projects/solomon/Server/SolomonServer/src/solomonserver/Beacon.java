package solomonserver;

public class Beacon
{
    private String id;
    private String label;
    private String name;
    public Beacon(String id, String label, String name)
    {
        this.id = id;
        this.label = label;
        this.name = name;
    }
    public Beacon(String name)
    {
        this.name = name;
    }
    public String getId()
    {
        return this.id;
    }
    public String getLabel()
    {
        return label;
    }
    public String getName()
    {
        return name;
    }
}