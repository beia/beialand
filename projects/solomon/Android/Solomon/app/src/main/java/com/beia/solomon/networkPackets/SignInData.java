package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class SignInData implements Serializable
{
  private final String username;
  private final String password;
  public SignInData(String username, String password)
  {
    this.username = username;
    this.password = password;
  }
  public String getUsername()
  {
    return this.username;
  }
  public String getPassword()
  {
    return this.password;
  }
  @Override
  public String toString()
  {
    return "Sign in data received:\n" + "Username: " + this.username + "\nPassword: " + this.password + "\n";
  }
}