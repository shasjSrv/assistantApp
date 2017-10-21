package com.example.jzy.helloword.decisionModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by xiashu on 17-10-10.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "hello,this is a alarm", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, DecisionServices.class);
        i.putExtra("command","alarm");
        context.startService(i);
    }
}