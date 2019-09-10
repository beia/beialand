package com.beia.solomon.runnables;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import com.beia.solomon.PreferencesActivity;

import java.util.Random;

public class UpdateImageLocationRunnable implements Runnable
{
    private LinearLayout preferenceImageLayout;
    private int imageWidth;
    private int imageHeight;
    private int[] location;
    private int displayWidth;
    private int displayHeight;
    private boolean preferenceSelected;
    private String preference;
    public UpdateImageLocationRunnable(LinearLayout preferenceImageLayout, int imageWidth, int imageHeight, int displayWidth, int displayHeight, boolean preferenceSelected, String preference)
    {
        this.preferenceImageLayout = preferenceImageLayout;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        location = new int[2];
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.preferenceSelected = preferenceSelected;
        this.preference = preference;
    }
    @Override
    public void run()
    {
        int locationX, locationY;
        int[] incrementXY = new int[2];
        Random random = new Random();
        int speed = 3;
        incrementXY[0] = (int) (speed + (Math.random() * speed + 1));
        incrementXY[1] = (int) ((-1 * speed) + (Math.random() * (-1 * speed) - 1));
        try
        {
            while (preferenceSelected == false)
            {
                preferenceImageLayout.getLocationOnScreen(location);
                locationX = location[0];
                locationY = location[1];
                //the preference layout would go outside the screen from the left
                if(locationX + incrementXY[0] < 0)
                {
                    //change the increment on the x axis to a positive one
                    incrementXY[0] = (int) (speed + (Math.random() * speed + 1));
                }
                //the preference layout would go outside the screen from the right
                if(locationX + incrementXY[0] > displayWidth - preferenceImageLayout.getMeasuredWidth())
                {
                    //change the increment on the x axis to a negative one
                    incrementXY[0] = (int) ((-1 * speed) + (Math.random() * (-1 * speed) - 1));
                }
                //the preference layout would go outside the screen from top
                if(locationY + incrementXY[1] < 0)
                {
                    //change the increment on the y axis to a positive one
                    incrementXY[1] = (int) (speed + (Math.random() * speed + 1));
                }
                //the preference layout would go outside the screen from bottom
                if(locationY + incrementXY[1] > 0.7 * displayHeight)
                {
                    //change the increment on the y axis to a negative one
                    incrementXY[1] = (int) ((-1 * speed) + (Math.random() * (-1 * speed) - 1));
                }
                //send a message to the UI thread to update the preference image layout location
                Message message;
                switch(preference)
                {
                    case "shoes":
                        message = PreferencesActivity.handler1.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler1.obtainMessage();
                        break;
                    case "electronics":
                        message = PreferencesActivity.handler2.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler2.obtainMessage();
                        break;
                    case "clothes":
                        message = PreferencesActivity.handler3.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler3.obtainMessage();
                        break;
                    case "food":
                        message = PreferencesActivity.handler4.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler4.obtainMessage();
                        break;
                    case "cofee":
                        message = PreferencesActivity.handler5.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler5.obtainMessage();
                        break;
                    case "sports":
                        message = PreferencesActivity.handler6.obtainMessage(1);
                        message.obj = incrementXY;
                        message.sendToTarget();
                        PreferencesActivity.handler6.obtainMessage();
                        break;
                    default:
                        break;
                }
                Thread.sleep(30);
            }
        }
        catch (InterruptedException e)
        {
                e.printStackTrace();
        }
    }
}
