package com.example.jzy.helloword;

import android.app.Application;

/**
 * Created by xiashu on 17-9-25.
 */

public class MyApplication extends Application {
    private static String userID = "user123";
    private static String userName = "patient";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setUserID(String id) {
        this.userID = id;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
}
