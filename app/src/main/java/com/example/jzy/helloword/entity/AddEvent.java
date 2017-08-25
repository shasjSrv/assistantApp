package com.example.jzy.helloword.entity;

/**
 * Created by jzy on 8/24/17.
 */

public class AddEvent {
    private String text;
    private int flag;
    public AddEvent(String text){
        this.text = text;
        this.flag = 1;
    }

    public int getFlag(){return this.flag;}
}
