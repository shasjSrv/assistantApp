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

    public static void setUserID(String id) {
        userID = id;
    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static String getUserID() {
        return userID;
    }

    public static String getUserName() {
        return userName;
    }
}
