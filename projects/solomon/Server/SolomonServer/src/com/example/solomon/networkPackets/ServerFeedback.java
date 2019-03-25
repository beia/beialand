package com.example.solomon.networkPackets;

import java.io.Serializable;

public class ServerFeedback implements Serializable
{
    private String feedbackMessage;
    public ServerFeedback(String feedbackMessage)
    {
        this.feedbackMessage = feedbackMessage;
    }
    public String getFeedbackMessage()
    {
        return this.feedbackMessage;
    }
}