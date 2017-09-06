package com.example.jzy.helloword.event;

import java.io.Serializable;

/**
 * MessageEvent实体类
 * Created by jzy on 2017/8/15.
 */

public class MessageEvent implements Serializable {

    private int userID;
    private int status;
    private int emojiID;
    private String result;


    public MessageEvent(int userID, int status, int emojiID) {
        this.userID = userID;
        this.status = status;
        this.emojiID = emojiID;
    }

    public MessageEvent(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        /*return "MessageEvent{" +
                "userID=" + userID +
                ", status=" + status +
                ", emojiID=" + emojiID +
                '}';*/
        return result;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEmojiID() {
        return emojiID;
    }

    public void setEmojiID(int emojiID) {
        this.emojiID = emojiID;
    }
}
