package runnables;

import data.Location;
import data.Point;

public class ComputeLocationRunnable implements Runnable {

    private Double[] beaconDistances;
    private Point[] closestBeaconsCoordinates;
    private Location[] partialLocations;
    private int threadID;

    public ComputeLocationRunnable(Double[] beaconDistances, Point[] closestBeaconsCoordinates, Location[] partialLocations, int threadID) {
        this.beaconDistances = beaconDistances;
        this.closestBeaconsCoordinates = closestBeaconsCoordinates;
        this.partialLocations = partialLocations;
        this.threadID = threadID;
    }

    @Override
    public void run() {
        double minError = 9999;
        double bestX = 0;
        double bestY = 0;
        double bestZ = 0;

        //find the lowest x,y,z values and the highest x,y,z values for the beaacons
        double min_x = 9999999, min_y = 9999999, min_z = 9999999;//minimum coordinates values
        double max_x = -99999, max_y = -99999, max_z = -99999;//maximum coordinates values
        for (int i = 0; i < closestBeaconsCoordinates.length; i++) {
            if(closestBeaconsCoordinates[i].getX() < min_x)
                min_x = closestBeaconsCoordinates[i].getX();
            if(closestBeaconsCoordinates[i].getY() < min_y)
                min_y = closestBeaconsCoordinates[i].getY();
            if(closestBeaconsCoordinates[i].getZ() < min_z)
                min_z = closestBeaconsCoordinates[i].getZ();
            if(closestBeaconsCoordinates[i].getX() > max_x)
                max_x = closestBeaconsCoordinates[i].getX();
            if(closestBeaconsCoordinates[i].getY() > max_y)
                max_y = closestBeaconsCoordinates[i].getY();
            if(closestBeaconsCoordinates[i].getZ() > max_z)
                max_z = closestBeaconsCoordinates[i].getZ();
        }

        //compute the partial locations
        for(double x = min_x + threadID; x < max_x; x+=partialLocations.length)//partialLocations.length - nrThreads
            for(double y = min_y; y < max_y; y++)
                for(double z = min_z; z < max_z; z++){
                    //compute error
                    double beacon0Distance = beaconDistances[0];
                    double beacon0X = closestBeaconsCoordinates[0].getX();
                    double beacon0Y = closestBeaconsCoordinates[0].getY();
                    double beacon0Z = closestBeaconsCoordinates[0].getZ();
                    double beacon0Error = Math.abs(Math.sqrt(Math.pow(x - beacon0X, 2) + Math.pow(y - beacon0Y, 2) + Math.pow(z - beacon0Z, 2)) - beacon0Distance);
                    double beacon1Distance = beaconDistances[1];
                    double beacon1X = closestBeaconsCoordinates[1].getX();
                    double beacon1Y = closestBeaconsCoordinates[1].getY();
                    double beacon1Z = closestBeaconsCoordinates[1].getZ();
                    double beacon1Error = Math.abs(Math.sqrt(Math.pow(x - beacon1X, 2) + Math.pow(y - beacon1Y, 2) + Math.pow(z - beacon1Z, 2)) - beacon1Distance);
                    double beacon2Distance = beaconDistances[2];
                    double beacon2X = closestBeaconsCoordinates[2].getX();
                    double beacon2Y = closestBeaconsCoordinates[2].getY();
                    double beacon2Z = closestBeaconsCoordinates[2].getZ();
                    double beacon2Error = Math.abs(Math.sqrt(Math.pow(x - beacon2X, 2) + Math.pow(y - beacon2Y, 2) + Math.pow(z - beacon2Z, 2)) - beacon2Distance);
                    double beacon3Distance = beaconDistances[3];
                    double beacon3X = closestBeaconsCoordinates[3].getX();
                    double beacon3Y = closestBeaconsCoordinates[3].getY();
                    double beacon3Z = closestBeaconsCoordinates[3].getZ();
                    double beacon3Error = Math.abs(Math.sqrt(Math.pow(x - beacon3X, 2) + Math.pow(y - beacon3Y, 2) + Math.pow(z - beacon3Z, 2)) - beacon3Distance);
                    double error = beacon0Error + beacon1Error + beacon2Error + beacon3Error;
                    if(error < minError) {
                        minError = error;
                        bestX = x;
                        bestY = y;
                        bestZ = z;
                    }
                }

        //System.out.println("BEST CARTESIAN LOCATION - threadID: " + threadID + "\nerror: " + minError + " x:" + bestX + " y:" + bestY + " z:" + bestZ);
        double bestLatitude = getLatitude(bestX, bestY, bestZ);
        double bestLongitude = getLongitude(bestX, bestY, bestZ);
        //System.out.println("BEST LAT-LNG POSITION - threadID: " + threadID + "\nlat:" + bestLatitude + " lon:" + bestLongitude);
        partialLocations[threadID] = new Location(bestLatitude, bestLongitude, minError);
    }

    public double getLatitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.asin(z / 6371000);
    }

    public double getLongitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.atan2(y, x);
    }
}
