package com.example.solomon.runnables;

import com.example.solomon.networkPackets.SignInData;
import com.example.solomon.networkPackets.SignUpData;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendAuthenticationDataRunnable implements Runnable
{
    private String actionLabel;
    private SignUpData signUpData;
    private SignInData signInData;
    private ObjectOutputStream objectOutputStream;
    public SendAuthenticationDataRunnable(String actionLabel, SignUpData signUpData, ObjectOutputStream objectOutputStream)
    {
        this.actionLabel = actionLabel;
        this.signUpData = signUpData;
        this.objectOutputStream = objectOutputStream;
    }
    public SendAuthenticationDataRunnable(String actionLabel, SignInData signInData, ObjectOutputStream objectOutputStream)
    {
        this.actionLabel = actionLabel;
        this.signInData = signInData;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run()
    {
        try
        {
            switch (actionLabel)
            {
                case "sign in":
                    objectOutputStream.writeObject(signInData);
                    break;
                case "sign up":
                    objectOutputStream.writeObject(signUpData);
                    break;
                default:
                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
