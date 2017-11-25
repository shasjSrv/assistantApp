package com.example.jzy.helloword;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.example.jzy.helloword.socketio.Socketio;

/**
 * Created by xiashu on 17-9-25.
 */

public class MyApplication extends Application {
    private static Context context;
    private static String userID = "user123";
    private static String userName = "patient";
    public static int NO_TYPE = 2;
    public static int PATIENT_TYPE = 0;
    public static int NURSE_TYPE = 1;
    private static int userType=NO_TYPE;

    public static Socketio socketio;
    //0表示病人 1表示护士 2表示未识别

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        socketio =  socketio = new Socketio();
        socketio.connect();
    }
    public static Socketio getSocketio(){return  socketio;}

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
