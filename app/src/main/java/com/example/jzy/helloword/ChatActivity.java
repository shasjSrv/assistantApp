package com.example.jzy.helloword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jzy.helloword.decisionModule.DecisionServices;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

/**
 * Created by jzy on 8/5/17.
 */

public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "demoService";
    public static Context context;
    private static Toast mToast;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*<<<<<<< HEAD
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

=======*/
        context = getApplicationContext();
        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(ChatActivity.this, param.toString());
        setContentView(R.layout.act_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        Intent i = new Intent(this, DecisionServices.class);
        Log.d(TAG, "before new startService");

        startService(i);

    }

    private void initValidationEt() {
        final MaterialEditText validationEt = (MaterialEditText) findViewById(R.id.validationEt);
        validationEt.addValidator(new RegexpValidator("Only Integer Valid!", "\\d+"));
        final Button validateBt = (Button) findViewById(R.id.validateBt);
        validateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate
                validationEt.validate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            Intent stopIntent = new Intent(this, DecisionServices.class);
            stopService(stopIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public static Context getContext(){
        return context;
    }

    public static void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

}
