package com.example.jzy.helloword.managerMedicineModule;

import android.os.Parcel;
import android.os.Parcelable;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by xiashu on 17-9-12.
 */

public class Patient implements Parcelable {
    private String name;
    private String id;
    private ArrayList<MedicineInfo> medicineInfos;
    private String patientRFID;


    public Patient(String id,String name,ArrayList<MedicineInfo> medicineInfos,String patientRFID){
        this.id = id;
        this.name = name;
        this.medicineInfos = medicineInfos;
        this.patientRFID=patientRFID;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPatientRFID(String patientRFID) { this.patientRFID=patientRFID; }

    public String getPatientRFID() { return patientRFID;}

    public ArrayList<MedicineInfo> getMedicineInfos(){return medicineInfos;}




   public static final Parcelable.Creator<Patient> CREATOR = new Creator<Patient>() {

       @Override
       public Patient createFromParcel(Parcel source) {
         //  Patient patient = new Patient(source.readString(),source.readString(),source.createTypedArrayList(MedicineInfo.CREATOR),source.readString());
           Patient patient = new Patient(source.readString(),source.readString(),source.createTypedArrayList(MedicineInfo.CREATOR),source.readString());
           /*patient.name = source.readString();
           patient.id = source.readString();*/
           return patient;
       }

       @Override
       public Patient[] newArray(int size) {
           return new Patient[size];
       }
   };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(id);
        out.writeTypedList(medicineInfos);
        out.writeString(patientRFID);

    }


}
