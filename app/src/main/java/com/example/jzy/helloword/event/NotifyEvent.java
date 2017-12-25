package com.example.jzy.helloword.event;

/**
 * Created by xiashu on 17-11-26.
 */

public class NotifyEvent {
    String notifyText;
    public NotifyEvent(String text) {
        this.notifyText = text;
    }

    public String getNotifyText(){return this.notifyText;}
}

