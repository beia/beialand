package com.beia.solomon.runnables;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class PostRunnable implements Runnable {
    private String postString;
    private ObjectOutputStream objectOutputStream;
    public PostRunnable(String postString, ObjectOutputStream objectOutputStream) {
        this.postString = postString;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try {
            objectOutputStream.writeObject(postString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
