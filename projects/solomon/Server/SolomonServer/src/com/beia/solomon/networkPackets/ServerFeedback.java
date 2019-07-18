package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class ServerFeedback implements Serializable
{
    private String feedbackMessage;
    private int userId;
    private String username;
    private String lastName;
    private String firstName;
    private int age;
    public ServerFeedback(String feedbackMessage)
    {
        this.feedbackMessage = feedbackMessage;
    }
    public ServerFeedback(String feedbackMessage, int userId, String username, String lastName, String firstName, int age)
    {
        this.feedbackMessage = feedbackMessage;
        this.userId = userId;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
    }
    public String getFeedbackMessage()
    {
        return this.feedbackMessage;
    }
    public int getUserId()
    {
        return this.userId;
    }
    public String getUsername()
    {
        return this.username;
    }
    public String getLastName()
    {
        return this.lastName;
    }
    public String getFirstName()
    {
        return this.firstName;
    }
    public int getAge()
    {
        return this.age;
    }
}