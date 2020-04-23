package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class ServerFeedback implements Serializable
{
    private String feedbackMessage;
    private int userId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private String gender;
    private int age;
    private boolean firstLogin;
    public ServerFeedback(String feedbackMessage)
    {
        this.feedbackMessage = feedbackMessage;
    }
    public ServerFeedback(String feedbackMessage, int userId, String username, String password, String lastName, String firstName, String gender, int age, boolean firstLogin)
    {
        this.feedbackMessage = feedbackMessage;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.age = age;
        this.firstLogin = firstLogin;
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
    public String getPassword() { return this.password; }
    public String getLastName()
    {
        return this.lastName;
    }
    public String getFirstName()
    {
        return this.firstName;
    }
    public String getGender() { return this.gender; }
    public int getAge()
    {
        return this.age;
    }
    public boolean isFirstLogin()
    {
        return this.firstLogin;
    }
}