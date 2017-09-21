package com.example.jzy.helloword.event;

/**
 * Created by jzy on 8/26/17.
 */

public class AddPatientEvent {
    private String text;
    private int flag;
    public AddPatientEvent(String text){
        this.text = text;
        this.flag = 1;
    }

    public int getFlag(){return this.flag;}
    public String getText(){return this.text;}
}
