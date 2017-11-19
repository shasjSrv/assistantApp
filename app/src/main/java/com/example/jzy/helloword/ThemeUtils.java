package com.example.jzy.helloword;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by xiashu on 17-11-19.
 */

public class ThemeUtils {
    private static int sTheme=0;

    public static void setsTheme(int i){
        sTheme=i;

    }

    public static int getTheme(){
        return sTheme;
    }

    public static void onActivityCreateSetTheme(Activity activity){
        switch (sTheme){
            case 0:
                activity.setTheme(R.style.AppTheme);
                break;
            case 1:
                activity.setTheme(R.style.NurseTheme);
                break;
        }


    }

    public static void changeToTheme(Activity activity,int theme){
            activity.finish();
            activity.startActivity(new Intent(activity,activity.getClass()));

    }

}
