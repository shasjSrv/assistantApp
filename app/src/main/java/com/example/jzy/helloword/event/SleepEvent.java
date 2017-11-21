package com.example.jzy.helloword.event;

import com.example.jzy.helloword.MyApplication;

/**
 * Created by jzy on 11/21/17.
 */

public class SleepEvent {
    public SleepEvent(){
        MyApplication.setUserType(MyApplication.NO_TYPE);
    }
}
