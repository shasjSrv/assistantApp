package com.example.jzy.helloword.event;

import java.util.ArrayList;

/**
 * Created by jzy on 9/16/17.
 */

public class NurseBackEvent {
    private int userID;

    private String userName;
    ArrayList<String>  patientNameArray;
    ArrayList<String>  patientIDArray;

    public NurseBackEvent(int userID, String userName,ArrayList<String>  patientNameArray,ArrayList<String> patientIDArray) {
        this.userID = userID;
        this.userName = userName;
        this.patientNameArray = patientNameArray;
        this.patientIDArray = patientIDArray;
    }

    public int getUserID(){return this.userID;}
    public String getUserName(){return this.userName;}
    public ArrayList<String > getPatientNameArray(){return this.patientNameArray;}
    public ArrayList<String > getpatientIDArray(){return this.patientIDArray;}

    @Override
    public String toString() {
        return userName;
    }
    public void getMessage(){

    }
}
