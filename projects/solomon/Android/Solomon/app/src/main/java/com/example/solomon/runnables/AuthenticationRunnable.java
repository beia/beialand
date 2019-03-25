package com.example.solomon.runnables;

import android.os.Message;

import com.example.solomon.LoginActivity;
import com.example.solomon.networkPackets.ServerFeedback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthenticationRunnable implements Runnable
{
    public ObjectInputStream objectInputStream;
    private boolean connected;
    public AuthenticationRunnable(ObjectInputStream objectInputStream)
    {
        this.objectInputStream = objectInputStream;
        this.connected = false;
    }
    @Override
    public void run() {
        try
        {
            while (!connected)
            {
                Object networkPacket = objectInputStream.readObject();

                //received a sign up request
                if(networkPacket instanceof ServerFeedback)
                {
                    ServerFeedback serverFeedback = (ServerFeedback)networkPacket;
                    Message message;
                    switch (serverFeedback.getFeedbackMessage())
                    {
                        case "username is taken":
                            message = LoginActivity.handler.obtainMessage(1);
                            message.obj = "username is taken";
                            message.sendToTarget();
                            break;
                        case "registered successfully":
                            message = LoginActivity.handler.obtainMessage(1);
                            message.obj = "registered succesfully";
                            message.sendToTarget();
                            break;
                        default:
                            break;
                    }
                }

            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
