package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class SignUpData implements Serializable
{
    private final String lastName;
    private final String firstName;
    private final int age;
    private final String username;
    private final String password;
    private final String passwordConfirmation;
    public SignUpData(String lastName, String firstName, int age, String username, String password, String passwordConfirmation)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
    public String getUsername()
    {
        return this.username;
    }
    public String getPassword()
    {
        return this.password;
    }
    public String getPasswordConfirmation()
    {
        return this.passwordConfirmation;
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
    @Override
    public String toString()
    {
        return "Sign up data received:\n" + "Username: " + this.username + "\nPassword: " + this.password + "\nLast name: " + this.lastName + "\nFirst name: " + this.firstName + "\nAge: " + this.age;
    }
}