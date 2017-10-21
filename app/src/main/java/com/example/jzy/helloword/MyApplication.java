package com.example.jzy.helloword;

import android.app.Application;
import android.content.Context;

/**
 * Created by xiashu on 17-9-25.
 */

public class MyApplication extends Application {
    private static Context context;
    private static String userID = "user123";
    private static String userName = "patient";

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    //获取全局context
    public static Context getContext(){
        return context;
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
