package com.example.jzy.helloword.managerMedicineModule;

import android.os.Parcel;
import android.os.Parcelable;



import java.util.ArrayList;

/**
 * Created by xiashu on 17-9-12.
 */

public class Patient implements Parcelable {
    private String name;
    private String id;
    private ArrayList<MedicineInfo> medicineInfos;


    Patient(String id,String name,ArrayList<MedicineInfo> medicineInfos){
        this.id = id;
        this.name = name;
        this.medicineInfos = medicineInfos;

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


   public static final Parcelable.Creator<Patient> CREATOR = new Creator<Patient>() {

       @Override
       public Patient createFromParcel(Parcel source) {
           Patient patient = new Patient(source.readString(),source.readString(),source.createTypedArrayList(MedicineInfo.CREATOR));
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

    }


}
