package com.beia.solomon.runnables;

import android.os.Message;

import com.beia.solomon.LoginActivity;
import com.beia.solomon.networkPackets.ServerFeedback;
import com.beia.solomon.networkPackets.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthenticationRunnable implements Runnable
{
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    private boolean connected;
    public AuthenticationRunnable(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream)
    {
        this.objectOutputStream = objectOutputStream;
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
                        case "can't login user":
                            message = LoginActivity.handler.obtainMessage(1);
                            message.obj = "username or password are wrong";
                            message.sendToTarget();
                            break;
                        case "login successful":
                            message = LoginActivity.handler.obtainMessage(2);
                            message.obj = new UserData(serverFeedback.getUserId(), serverFeedback.getUsername(), serverFeedback.getPassword(), serverFeedback.getLastName(), serverFeedback.getFirstName(), serverFeedback.getAge(), serverFeedback.isFirstLogin());
                            this.connected = true;
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
