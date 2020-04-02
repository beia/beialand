package com.beia.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.beia.solomon.receivers.NotificationReceiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.beia.solomon.runnables.WaitForServerDataRunnable.ip;
import static com.beia.solomon.runnables.WaitForServerDataRunnable.port;

public class RequestRunnable implements Runnable
{
    private String request;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private boolean connected;
    public RequestRunnable(String request) throws IOException {//NOTIFICATION REQUEST IN BACKGROUND
        this.connected = false;
        this.request = request;
    }
    public RequestRunnable(String request, ObjectOutputStream objectOutputStream)
    {
        this.connected = true;
        this.request = request;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try
        {
            if(!connected)//NOTIFICATION
            {
                //the app is in background it is offline so we need to connect first
                //send a notification request to the server
                //receive a response
                Socket socket = new Socket(ip, port);
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());
                synchronized (objectOutputStream) {
                    this.objectOutputStream.writeObject(request);
                    String response = (String) objectInputStream.readObject();
                    Log.d("RESPONSE", response);
                    Message message = NotificationReceiver.handler.obtainMessage();
                    message.what = 1;
                    message.obj = response;
                    message.sendToTarget();
                }
                objectInputStream.close();
                objectOutputStream.close();
                socket.close();
            }
            else//the app is already connected to the server
            {
                synchronized (objectOutputStream) {
                    this.objectOutputStream.writeObject(request);
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
