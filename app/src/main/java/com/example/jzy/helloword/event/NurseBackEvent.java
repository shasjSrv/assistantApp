package com.example.jzy.helloword.event;

/**
 * Created by jzy on 9/16/17.
 */

public class NurseBackEvent {
    private int userID;

    private String userName;

    public NurseBackEvent(int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public int getUserID(){return this.userID;}

    @Override
    public String toString() {
        return userName;
    }
    public void getMessage(){

    }
}
