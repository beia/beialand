package com.beia.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.data.Point;
import com.google.android.gms.maps.model.LatLng;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

public class ComputePositionRunnable implements Runnable
{
    public IBeaconDevice[] closestBeacons;
    public Point[] closestBeaconsCoordinates;
    public ComputePositionRunnable(IBeaconDevice[] closestBeacons, Point[] closestBeaconsCoordinates) {
        this.closestBeacons = closestBeacons;
        this.closestBeaconsCoordinates = closestBeaconsCoordinates;
    }
    @Override
    public void run() {
        double minError = 9999;
        double bestX = 0;
        double bestY = 0;
        double bestZ = 0;
        for(double x = closestBeaconsCoordinates[0].getX(); x < closestBeaconsCoordinates[2].getX(); x+=2)
            for(double y = closestBeaconsCoordinates[3].getY(); y < closestBeaconsCoordinates[1].getY(); y+=2)
                for(double z = closestBeaconsCoordinates[1].getZ(); z < closestBeaconsCoordinates[3].getZ(); z+=2){
                    //compute error
                    double beacon0Distance = closestBeacons[0].getDistance();
                    double beacon0X = closestBeaconsCoordinates[0].getX();
                    double beacon0Y = closestBeaconsCoordinates[0].getY();
                    double beacon0Z = closestBeaconsCoordinates[0].getZ();
                    double beacon0Error = Math.abs(Math.sqrt(Math.pow(x - beacon0X, 2) + Math.pow(y - beacon0Y, 2) + Math.pow(z - beacon0Z, 2)) - beacon0Distance);
                    double beacon1Distance = closestBeacons[1].getDistance();
                    double beacon1X = closestBeaconsCoordinates[1].getX();
                    double beacon1Y = closestBeaconsCoordinates[1].getY();
                    double beacon1Z = closestBeaconsCoordinates[1].getZ();
                    double beacon1Error = Math.abs(Math.sqrt(Math.pow(x - beacon1X, 2) + Math.pow(y - beacon1Y, 2) + Math.pow(z - beacon1Z, 2)) - beacon1Distance);
                    double beacon2Distance = closestBeacons[2].getDistance();
                    double beacon2X = closestBeaconsCoordinates[2].getX();
                    double beacon2Y = closestBeaconsCoordinates[2].getY();
                    double beacon2Z = closestBeaconsCoordinates[2].getZ();
                    double beacon2Error = Math.abs(Math.sqrt(Math.pow(x - beacon2X, 2) + Math.pow(y - beacon2Y, 2) + Math.pow(z - beacon2Z, 2)) - beacon2Distance);
                    double beacon3Distance = closestBeacons[3].getDistance();
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
        Log.d("BEST: ", "error: " + minError + " x:" + bestX + " y:" + bestY + " z:" + bestZ);

        double bestLatitude = getLatitude(bestX, bestY, bestZ);
        double bestLongitude = getLongitude(bestX, bestY, bestZ);
        Log.d("BEST:", "lat:" + bestLatitude + " lon:" + bestLongitude);

        Message message = MainActivity.mainActivityHandler.obtainMessage();
        message.what = 4;
        message.obj = new LatLng(bestLatitude, bestLongitude);
        message.sendToTarget();
    }
    public double getLatitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.asin(z / 6371000);
    }
    public double getLongitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.atan2(y, x);
    }
}
