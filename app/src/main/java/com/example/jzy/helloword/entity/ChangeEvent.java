package com.example.jzy.helloword.entity;

/**
 * Created by jzy on 8/26/17.
 */

public class ChangeEvent {
    private String text;
    private int flag;
    public ChangeEvent(String text){
        this.text = text;
        this.flag = 1;
    }

    public int getFlag(){return this.flag;}
    public String getText(){return this.text;}
}
