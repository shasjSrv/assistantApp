package com.example.jzy.helloword.entity;

/**
 * Created by jzy on 8/24/17.
 */

public class backEnvent {
    private int userID;
    private int status;
    private int emojiID;
    private String userName;
    private String text;


    public backEnvent(String text){this.text = text;}
    public backEnvent(int userID, int status, int emojiID,String userName) {
        this.userID = userID;
        this.status = status;
        this.emojiID = emojiID;
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
