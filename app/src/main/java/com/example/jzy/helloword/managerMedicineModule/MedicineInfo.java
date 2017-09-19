package com.example.jzy.helloword.managerMedicineModule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jzy on 9/19/17.
 */

class MedicineInfo implements Parcelable {
    String medicineName;
    String medicineCount;
    String medicineDosage;

    MedicineInfo(String medicineName,String medicineCount,String medicineDosage){
        this.medicineName = medicineName;
        this.medicineCount = medicineCount;
        this.medicineDosage = medicineDosage;
    }

    public String getMedicineName(){return this.medicineName;}

    public String getMedicineCount(){return this.medicineCount;}

    public String getMedicineDosage(){return this.medicineDosage;}

    public final static Parcelable.Creator<MedicineInfo> CREATOR = new Creator<MedicineInfo>() {
        @Override
        public MedicineInfo createFromParcel(Parcel source) {
            MedicineInfo medicineInfo = new MedicineInfo(source.readString(),source.readString(),source.readString());
            return medicineInfo;
        }

        @Override
        public MedicineInfo[] newArray(int size) {
            return new MedicineInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(medicineName);
        parcel.writeString(medicineCount);
        parcel.writeString(medicineDosage);

    }

};