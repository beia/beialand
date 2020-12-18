package com.beia.solomon_smart_shopping.model;

public class BeaconLocalizationData {
    private Point[] beaconCoordinates;
    private double[] beaconDistances;

    public BeaconLocalizationData(Point[] beaconCoordinates, double[] beaconDistances) {
        this.beaconCoordinates = beaconCoordinates;
        this.beaconDistances = beaconDistances;
    }

    public Point[] getBeaconCoordinates() {
        return beaconCoordinates;
    }

    public void setBeaconCoordinates(Point[] beaconCoordinates) {
        this.beaconCoordinates = beaconCoordinates;
    }

    public double[] getBeaconDistances() {
        return beaconDistances;
    }

    public void setBeaconDistances(double[] beaconDistances) {
        this.beaconDistances = beaconDistances;
    }
}
