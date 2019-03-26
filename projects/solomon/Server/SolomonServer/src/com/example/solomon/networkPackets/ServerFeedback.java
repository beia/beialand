package com.example.solomon.networkPackets;

import java.io.Serializable;

public class ServerFeedback implements Serializable
{
    private String feedbackMessage;
    private int userId;
    public ServerFeedback(String feedbackMessage)
    {
        this.feedbackMessage = feedbackMessage;
    }
    public ServerFeedback(String feedbackMessage, int userId)
    {
        this.feedbackMessage = feedbackMessage;
        this.userId = userId;
    }
    public String getFeedbackMessage()
    {
        return this.feedbackMessage;
    }
    public int getUserId()
    {
        return this.userId;
    }
}