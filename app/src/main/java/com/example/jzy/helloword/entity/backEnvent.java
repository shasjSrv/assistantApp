package com.example.jzy.helloword.entity;

/**
 * Created by jzy on 8/24/17.
 */

public class backEnvent {
    private int userID;
    private int status;
    private int emojiID;
    private String text;


    public backEnvent(String text){this.text = text;}
    public backEnvent(int userID, int status, int emojiID) {
        this.userID = userID;
        this.status = status;
        this.emojiID = emojiID;
    }

    public int getUserID(){return this.userID;}

    @Override
    public String toString() {
        return "Tip{" +
                "userID=" + userID +
                '}';
    }
    public void getMessage(){

    }

}
