package com.example.jzy.helloword.videoModule;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.jzy.helloword.HomePageActivity;
import com.example.jzy.helloword.Keys;
import com.example.jzy.helloword.R;
import com.example.jzy.helloword.event.BackPressedEvent;
import com.example.jzy.helloword.event.NurseBackEvent;
import com.example.jzy.helloword.event.PatientBackEnvent;
import com.example.jzy.helloword.event.AddPatientEvent;
import com.example.jzy.helloword.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jzy on 8/2/17.
 */

public class VideoActivity extends AppCompatActivity {
    private static Context context;
    private static final String TAG = VideoActivity.class.getSimpleName();
    private Camera mCamera;
    private Preview mPreview;
    private Context ctx;
    private String keyprefRoomServerUrl;
    private String keyprefUserInfoServerUrl;
    private SharedPreferences sharedPref;
    private RemindDialog remindDialog;
    private int flag;
    ProgressDialog progressDialog;
    private Handler uiHandler = new Handler() {

        //得到用户信息后显示出来，用户确认后发送脸部信息
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d("Syss", "message: " + msg.what);
                    final JSONObject result= (JSONObject) msg.obj;

                    View view = (View) getLayoutInflater().inflate(R.layout.dialog_show_infor, null);
                    TextView userName=(TextView)view.findViewById(R.id.show_name);
                    userName.setText("hi here");
                    TextView userGeneder=(TextView)view.findViewById(R.id.show_gender);
                    TextView userAge=(TextView)view.findViewById(R.id.show_age);
                    TextView userRoom=(TextView)view.findViewById(R.id.show_roomid);
                    TextView userBed=(TextView)view.findViewById(R.id.show_bedid);
                    TextView userDiagnoseid=(TextView)view.findViewById(R.id.show_diagnoseid);

                    try {
                        Log.d("Syss","json: "+result.toString());
                        Log.d("Syss","username: "+result.getString("username"));
                        userName.setText(result.getString("username"));
                        userGeneder.setText(result.getString("gender"));
                        int a =result.getInt("age");
                        Log.d("Syss","aaa:  "+a);
                        userAge.setText(""+result.getInt("age"));
                        userRoom.setText(""+result.getInt("roomNo"));
                        userBed.setText(""+result.getInt("berthNo"));
                        userDiagnoseid.setText(result.getString("diagnoseId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        progressDialog.dismiss();
                    }


                    TextView btn_sure = (TextView) view.findViewById(R.id.btn_sure);
                    final AlertDialog dialog = new AlertDialog.Builder(context).setView(view)
                            .create();

                    btn_sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Syss", "here----->");
                            dialog.dismiss();
                            try {
                                //切换为当前用户
                                mPreview.setUserId(result.getString("diagnoseId"), 1);

                                //向服务器发送脸部信息
                                //。。。。。。。。。。。。。。。


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    TextView btn_cancel=(TextView)view.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    //去掉黑色边角
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_video);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventBus.getDefault().register(this);
        progressDialog=new ProgressDialog(VideoActivity.this);
        /**
         * get the parameter of URL
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        String roomURL = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));
        keyprefUserInfoServerUrl = getString(R.string.pref_user_info_ip_key);
        String userInfoURL = sharedPref.getString(
                keyprefUserInfoServerUrl, getString(R.string.pref_user_info_ip_default));
        Bundle bundle = this.getIntent().getExtras();
        flag = bundle.getInt("flag");
        mPreview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView), roomURL, flag, userInfoURL);
        mPreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //enterUserInfo();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                mCamera = Camera.open(1);
                mCamera.startPreview();
                mPreview.setCamera(mCamera);

            } catch (RuntimeException ex) {
                Toast.makeText(context, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }


    private void enterUserInfo() {
        final AlertDialog.Builder remindINfor = new AlertDialog.Builder(VideoActivity.this);
        remindINfor.setTitle("未识别成功\n是否要添加新的人脸信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //添加人脸
                        View view = (View) getLayoutInflater().inflate(R.layout.dialog_add_face, null);
                        final EditText inputId = (EditText) view.findViewById(R.id.inputId);
                        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).setPositiveButton("确定", null)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(inputId.getText())) {
                                    inputId.setError("输入不能为空");
                                    return;
                                }
                                Toast.makeText(context, "ID: " + inputId.getText().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.show();
                                QueryUserInfoThread queryUserInfoThread = new QueryUserInfoThread("", inputId.getText().toString(), uiHandler);
                                queryUserInfoThread.start();

                                dialog.dismiss();

                            }
                        });
                    }
                })
                .setNegativeButton("取消", null);

        remindINfor.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddPatientEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.getText());
        /*
        show enterUserID dialog
        */
        enterUserInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        showTip(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PatientBackEnvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        backToHomePage(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NurseBackEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        backToHomePage(event.toString());
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

    public static void showTip(final String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Log.d("Sys", "i am back");
        backToHomePage("My name is linjiqin");
        EventBus.getDefault().post(new BackPressedEvent(""));
    }
}
