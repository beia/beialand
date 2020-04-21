package runnables;

import com.beia.solomon.networkPackets.BeaconTime;
import solomonserver.SolomonServer;

import java.util.HashMap;

public class ComputeTotalBeaconTimeRunnable implements Runnable //compute the total time spent by users for each beacon
{
    public static long nrThreads = 6;
    @Override
    public void run() {
        try {
            while (true) {
                HashMap<Integer, HashMap<String, BeaconTime>> usersBeaconTimeMap = SolomonServer.usersBeaconTimeMap;
                HashMap<String, Long> totalBeaconTime = new HashMap<>();
                for(int i = 0; i < nrThreads; i++) {//parallelize task
                    final int threadIndex = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).start();
                }
                Thread.sleep(5 * 60 * 1000);//5 minutes
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
