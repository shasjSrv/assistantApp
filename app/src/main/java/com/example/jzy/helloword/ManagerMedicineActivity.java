package com.example.jzy.helloword;

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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.jzy.helloword.xmlrpcLib.XMLRPCClient;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCException;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jzy on 8/5/17.
 */

public class ManagerMedicineActivity extends AppCompatActivity {
    public static final String TAG = "demoService";
    public static Context context;
    private static Toast mToast;
    private String keyPrefBoxIP;
    private SharedPreferences sharedPref;
    private String BoxIP;

    private MaterialEditText validationEt;//房间号
    private MaterialEditText bedNum;//床位
    Button validateBt;
    boolean checkInput = true;//检测输入是否为空


    private RecyclerView recyclerView;
    private List<Patient> patientList=new ArrayList<>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manage_medicine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // getSupportActionBar().setDisplayShowTitleEnabled(true);

      /*  validationEt = (MaterialEditText) findViewById(R.id.validationEt);
        bedNum = (MaterialEditText) findViewById(R.id.bedNum);
        validateBt = (Button) findViewById(R.id.validateBt);
        initValidationEt();

*/

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view) ;


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initPatients();
        PatientsAdapter adapter=new PatientsAdapter(patientList,ManagerMedicineActivity.this);
        recyclerView.setAdapter(adapter);




    }

    private void initPatients(){

        for(int i=0;i<4;i++)
        {
            Patient patient=new Patient("id-"+i,"name"+i,"medicineinfor"+i);
            patientList.add(patient);
        }

    }

    private void initValidationEt() {


        //final MaterialEditText validationEt = (MaterialEditText) findViewById(R.id.validationEt);
        // validationEt.addValidator(new RegexpValidator("仅可输入数字!", "\\d+"));
        //final Button validateBt = (Button) findViewById(R.id.validateBt);
        /**
         * get the parameter of xmrRpcServer
         */

    /*    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyPrefBoxIP = getString(R.string.pref_box_ip_key);
        BoxIP = sharedPref.getString(
                keyPrefBoxIP, getString(R.string.pref_box_ip_default));*/

  /*      validateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate
                //点击后检测输入是否为空
                checkInput = true;
                if (validationEt.getText().length() <= 0) {
                    validationEt.setError("请输入房间号");
                    checkInput = false;
                }
                if (bedNum.getText().length() <= 0) {
                    bedNum.setError("请输入床位号");
                    checkInput = false;
                }

                //若输入不为空
                if (checkInput) {
                    //如果药盒已满则弹出提示框
                    boolean checkFull = true;
                    if (checkFull) {
                        final MaterialDialog mMaterialDialog = new MaterialDialog(ManagerMedicineActivity.this);
                        mMaterialDialog.setMessage("药盒已满")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                });

                        mMaterialDialog.show();
                    }
                    //否则进行房间号验证 然后放药
                    else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                XMLRPCClient client = new XMLRPCClient(BoxIP);
                                try {
                                    Log.i(TAG, "int: " + validationEt.getText().toString());
                                    int result = (Integer) client.call("QueryMedicine", "94E316");
                                    Log.i("XMLRPC Test", "QueryMedicine 94E316 result  = " + result);
                                    result = (Integer) client.call("QueryMedicine", "94AA92");
                                    Log.i("XMLRPC Test", "QueryMedicine 94AA92 result  = " + result);
                                    result = (Integer) client.call("QueryMedicine", "9576AA");
                                    Log.i("XMLRPC Test", "QueryMedicine 9576AA result  = " + result);
                                    result = (Integer) client.call("QueryMedicine", "957B3D");
                                    Log.i("XMLRPC Test", "QueryMedicine 957B3D rresult  = " + result);
                                    result = (Integer) client.call("Put", "94E316");
                                    Log.i("XMLRPC Test", "Put 94E316 result  = " + result);
                                    result = (Integer) client.call("Put", "94AA92");
                                    Log.i("XMLRPC Test", "Put 94AA92 result  = " + result);
                                    result = (Integer) client.call("Put", "9576AA");
                                    Log.i("XMLRPC Test", "Put 9576AA result  = " + result);
                                    result = (Integer) client.call("Put", "957B3D");
                                    Log.i("XMLRPC Test", "Put 957B3D result  = " + result);
                                    result = (Integer) client.call("Put", "94E316");
                                    Log.i("XMLRPC Test", "Put 94E316 result  = " + result);
                                    result = (Integer) client.call("Put", "94AA92");
                                    Log.i("XMLRPC Test", "Put 94AA92 result  = " + result);
                                    result = (Integer) client.call("Put", "9576AA");
                                    Log.i("XMLRPC Test", "Put 9576AA result  = " + result);
                                    result = (Integer) client.call("Put", "957B3D");
                                    Log.i("XMLRPC Test", "Put 957B3D result  = " + result);
                                    result = (Integer) client.call("QueryAvailable");
                                    Log.i("XMLRPC Test", "QueryAvailable remain result  = " + result);

                                } catch (XMLRPCException e) {
                                    Log.i("XMLRPC Test", "Error", e);
                                }
                            }
                        }).start();

                    }
                }

            }
        });*/
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            *//*Intent stopIntent = new Intent(this, DecisionServices.class);
            stopService(stopIntent);
            finish();*//*
        }
        return super.onOptionsItemSelected(item);
    }*/
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
        }
        return super.onOptionsItemSelected(item);
    }

}
