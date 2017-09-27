package com.example.jzy.helloword.event;

/**
 * Created by jzy on 9/27/17.
 */

public class AddPatientSuccEvent {
    private String name;
    public AddPatientSuccEvent(String name){
        this.name = name;
    }

    public String getName(){return this.name;}
}
