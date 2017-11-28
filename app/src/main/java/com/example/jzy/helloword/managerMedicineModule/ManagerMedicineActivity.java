package com.example.jzy.helloword.managerMedicineModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.jzy.helloword.Keys;
import com.example.jzy.helloword.MyApplication;
import com.example.jzy.helloword.R;

import com.example.jzy.helloword.event.BackPressedEvent;
import com.example.jzy.helloword.event.NotifyEvent;
import com.rengwuxian.materialedittext.MaterialEditText;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzy on 8/5/17.
 */

public class ManagerMedicineActivity extends AppCompatActivity {
    public static final String TAG = "demoService";
    public static Context context;
    private static Toast mToast;
    private static final int CONNECT_Video = 99;


    private MaterialEditText validationEt;//房间号
    private MaterialEditText bedNum;//床位
    Button validateBt;
    boolean checkInput = true;//检测输入是否为空

    private SharedPreferences sharedPref;
    private String keyprefUserInfoServerUrl;
    private String keyPrefBoxIP;
    private String medicineBoxIP;
    private String userInfoURL;

    private RecyclerView recyclerView;
    private List<Patient> patientList=new ArrayList<>();
    PatientsAdapter adapter;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manage_medicine);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange)  );
        actionBar.setTitle("护士："+ MyApplication.getUserName());
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // getSupportActionBar().setDisplayShowTitleEnabled(true);

      /*  validationEt = (MaterialEditText) findViewById(R.id.validationEt);
        bedNum = (MaterialEditText) findViewById(R.id.bedNum);
        validateBt = (Button) findViewById(R.id.validateBt);
        initValidationEt();
*/
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        keyprefUserInfoServerUrl = getString(R.string.pref_user_info_ip_key);
        userInfoURL = sharedPref.getString(
                keyprefUserInfoServerUrl, getString(R.string.pref_user_info_ip_default));
        keyPrefBoxIP = getString(R.string.pref_box_ip_key);
        medicineBoxIP = sharedPref.getString(
                keyPrefBoxIP, getString(R.string.pref_box_ip_default));

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view) ;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initPatients();
        adapter =new PatientsAdapter(patientList,ManagerMedicineActivity.this,medicineBoxIP,userInfoURL);
        recyclerView.setAdapter(adapter);

    }

    private void initPatients(){
        Bundle bundle = this.getIntent().getExtras();
        ArrayList<Patient> patientArray=bundle.getParcelableArrayList("patientArray");
        Log.d("test","patientCount: "+patientArray.size());
        patientList=patientArray;
    }

    private void updateSendMedicineInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


    public static Context getContext() {
        return context;
    }


    /**
     * 返回主页
     */
    private void backToHomePage(String text) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(Keys.videoResult, text);
        //设置返回数据
        setResult(Keys.VIDEO_RESULT, intent);
        //关闭Activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            backToHomePage("My name is linjiqin");
            EventBus.getDefault().post(new BackPressedEvent(""));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d("Sys","i am back");
        backToHomePage("My name is linjiqin");
        EventBus.getDefault().post(new BackPressedEvent(""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"return "+requestCode+" "+resultCode);
       switch (requestCode){
           case CONNECT_Video:

               String resultDate=data.getStringExtra("data_return");
               Log.d(TAG,"return from p2p------------>"+resultDate);
               if(resultCode==RESULT_OK){
                   Toast.makeText(context,"return from video,oh yeah",Toast.LENGTH_LONG).show();
                   EventBus.getDefault().post(new NotifyEvent());
                   finish();
               }
                break;

       }
    }
}
