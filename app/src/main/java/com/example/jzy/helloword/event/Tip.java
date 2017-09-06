package com.example.jzy.helloword.event;

/**
 * Created by jzy on 8/21/17.
 */

public class Tip {
    private int userID;
    private int status;
    private int emojiID;
    private String text;


    public Tip(String text){this.text = text;}
    public Tip(int userID, int status, int emojiID) {
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
