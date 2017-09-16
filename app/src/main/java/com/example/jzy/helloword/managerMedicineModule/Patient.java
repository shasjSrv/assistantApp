package com.example.jzy.helloword.managerMedicineModule;

/**
 * Created by xiashu on 17-9-12.
 */

public class Patient {
    String name;
    String id;
    String medicine_infor;

    Patient(String id,String name,String infor){
        this.id=id;
        this.name=name;
        this.medicine_infor=infor;

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

    public String getMedicine_infor() {
        return medicine_infor;
    }

    public void setMedicine_infor(String medicine_infor) {
        this.medicine_infor = medicine_infor;
    }
}
