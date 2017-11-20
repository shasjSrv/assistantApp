package com.example.jzy.helloword;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by xiashu on 17-9-25.
 */

public class MyApplication extends Application {
    private static Context context;
    private static String userID = "user123";
    private static String userName = "patient";
    private static int userType=2;
    //0表示病人 1表示护士 2表示未识别

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

    public static int getUserType() {
        return userType;
    }

    public static void setUserType(int userType) {
        MyApplication.userType = userType;
    }

}
