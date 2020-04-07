package com.beia.solomon.data;

//class point is used for the A* algorithm
public class Point
{
    private double x;
    private double y;
    private double z;
    public Point(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
}
