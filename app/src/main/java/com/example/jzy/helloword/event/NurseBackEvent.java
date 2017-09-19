package com.example.jzy.helloword.event;

import com.example.jzy.helloword.managerMedicineModule.Patient;

import java.util.ArrayList;

/**
 * Created by jzy on 9/16/17.
 */

public class NurseBackEvent {
    private int nurseID;

    private String userName;
    ArrayList<Patient> patientArray;
    //ArrayList<String>  patientNameArray;
    //ArrayList<String>  patientIDArray;

    public NurseBackEvent(int userID, String userName,ArrayList<Patient>  patientArray) {
        this.nurseID = userID;
        this.userName = userName;
        this.patientArray=patientArray;
        //this.patientNameArray = patientNameArray;
        //this.patientIDArray = patientIDArray;
    }

    public int getUserID(){return this.nurseID;}
    public String getUserName(){return this.userName;}
    public ArrayList<Patient> getUserPatiens(){return this.patientArray;}
   // public ArrayList<String > getPatientNameArray(){return this.patientNameArray;}
   // public ArrayList<String > getpatientIDArray(){return this.patientIDArray;}

    @Override
    public String toString() {
        return userName;
    }
    public void getMessage(){

    }
}
