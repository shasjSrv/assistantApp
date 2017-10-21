package com.example.jzy.helloword;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jzy.helloword.decisionModule.Alarm;
import com.example.jzy.helloword.decisionModule.DecisionServices;
import com.example.jzy.helloword.event.AddPatientEvent;
import com.example.jzy.helloword.event.BackPressedEvent;
import com.example.jzy.helloword.event.NurseBackEvent;
import com.example.jzy.helloword.event.Tip;
import com.example.jzy.helloword.managerMedicineModule.ManagerMedicineActivity;
import com.example.jzy.helloword.managerMedicineModule.MedicineInfo;
import com.example.jzy.helloword.managerMedicineModule.Patient;
import com.example.jzy.helloword.videoModule.QueryUserInfoThread;
import com.example.jzy.helloword.videoModule.RemindDialog;
import com.example.jzy.helloword.videoModule.VideoActivity;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = HomePageActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST = 1;
    private Button btnVideo, btnChat, btnDiagnose;
    private ImageView ivWelcome;
    private Timer timer;
    private TimerTask timerTask;
    private int count = 0;
    private static Context context;
    private RemindDialog remindDialog;
    private String keyPrefBoxIP;
    private SharedPreferences sharedPref;
    private String BoxIP;
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
                        // Log.d("Syss","username: "+result.getString("username"));
                        userName.setText(result.getString("userName"));
                        userGeneder.setText(result.getString("gender"));
                        userAge.setText(""+result.getInt("age"));
                        userRoom.setText(""+result.getInt("roomNo"));
                        userBed.setText(""+result.getInt("berthNo"));
                        userDiagnoseid.setText(result.getString("userID"));

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
                            jumpToVideoActivity(1);
                            /*try {
                                //切换为当前用户
//                                mPreview.setUserId(result.getString("diagnoseId"), 1);

                                //向服务器发送脸部信息
                                //。。。。。。。。。。。。。。。


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                        }
                    });
                    TextView btn_cancel=(TextView)view.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new BackPressedEvent(""));
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



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int myCount = Integer.valueOf(msg.obj.toString());

            switch (msg.what) {
                case 1:
                    setImageViewSrc(myCount);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_homepage);
        init();



    }

    void startAnotherApp(String packageName,String className){
       // String packageName="com.example.xiashu.xiashu_test";
        //String className="com.example.xiashu.xiashu_test.MainActivity";
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BoxIP = sharedPref.getString(
                keyPrefBoxIP, getString(R.string.pref_box_ip_default));
        if (timerTask == null) {
            timer = new Timer();
            //延迟一秒，迭代一秒设置图片
            timerTask = new TimerTask() {

                @Override
                public void run() {
                    ++count;
                    handler.sendMessage(handler.obtainMessage(1, count));
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        } else {
            handler.sendMessage(handler.obtainMessage(1, count));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Intent stopIntent = new Intent(this, DecisionServices.class);
        stopService(stopIntent);
    }

    private void init() {
        EventBus.getDefault().register(this);


        progressDialog=new ProgressDialog(HomePageActivity.this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyPrefBoxIP = getString(R.string.pref_box_ip_key);
        BoxIP = sharedPref.getString(
                keyPrefBoxIP, getString(R.string.pref_box_ip_default));


//        btnVideo = (Button) findViewById(R.id.btn_video);
        btnChat = (Button) findViewById(R.id.btn_chat);
//        btnDiagnose = (Button) findViewById(R.id.btn_diagnose);
//        btnVideo.setOnClickListener(this);
        btnChat.setOnClickListener(this);
//        btnDiagnose.setOnClickListener(this);

        ivWelcome = (ImageView) findViewById(R.id.iv_welcome);

        StringBuffer param = new StringBuffer();
        param.append("appid=").append(getString(R.string.app_id)).append(",").append(SpeechConstant.ENGINE_MODE).append("=").append(SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(HomePageActivity.this, param.toString());
        Intent i = new Intent(this, DecisionServices.class);
        Log.d(TAG, "before new startService");
        startService(i);

        //start alarm
        Alarm.startRemind();

    }

    /**
     * 检查运行时权限（相机权限、文件写权限）
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            jumpToVideoActivity(1);
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }

    /**
     * 跳转至VideoActivity
     */
    private void jumpToVideoActivity(int flag) {
        Intent intent = new Intent(HomePageActivity.this, VideoActivity.class);
        Bundle bundle = new Bundle();
        /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/

        bundle.putInt("flag",flag);
        /*把bundle对象assign给Intent*/
        intent.putExtras(bundle);
        startActivityForResult(intent, Keys.VIDEO_REQUEST);
    }

    /**
     * 跳转至ChatActivity
     */
    private void jumpToManagerMedicineActivity(NurseBackEvent event) {
        Intent intent = new Intent(HomePageActivity.this, ManagerMedicineActivity.class);
        Bundle bundle = new Bundle();
       // bundle.putStringArrayList("patientName",event.getPatientNameArray());
       // bundle.putStringArrayList("patientID",event.getpatientIDArray());
        bundle.putParcelableArrayList("patientArray",event.getUserPatiens());
        /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/

//        bundle.putInt("flag",flag);
        /*把bundle对象assign给Intent*/

        intent.putExtras(bundle);
        startActivityForResult(intent, Keys.CHAT_REQUEST);
    }

    /**
     * 跳转至DiagnoseActivity
     */
    private void jumpToDiagnoseActivity() {
        Intent intent = new Intent(HomePageActivity.this, DiagnoseActivity.class);
        startActivity(intent);
    }

    /**
     * 根据count循环对ImageView设置图片
     *
     * @param count
     */
    private void setImageViewSrc(int count) {
        int myCount = count % 4;
        switch (myCount) {
            case 0:
                ivWelcome.setImageResource(R.drawable.close_eye);
                break;
            case 1:
                ivWelcome.setImageResource(R.drawable.smile);
                break;
            case 2:
                ivWelcome.setImageResource(R.drawable.smile2);
                break;
            case 3:
                ivWelcome.setImageResource(R.drawable.laugh);
                break;
            default:
                break;
        }
    }

    /**
     * 销毁TimerTask和Timer
     */
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    private void enterUserInfo() {
        final AlertDialog.Builder remindINfor = new AlertDialog.Builder(HomePageActivity.this);
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
                                        EventBus.getDefault().post(new BackPressedEvent(""));
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
                                QueryUserInfoThread queryUserInfoThread = new QueryUserInfoThread(getString(R.string.pref_user_info_ip_default), inputId.getText().toString(), uiHandler);
                                queryUserInfoThread.start();

                                dialog.dismiss();

                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i){
                        EventBus.getDefault().post(new BackPressedEvent(""));
                    }
                });

        remindINfor.show();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddPatientEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.getText());
        /*
        show enterUserID dialog
        */
//        backToHomePage("");
        enterUserInfo();
    }

    /**
     * 跳转VideoActivity人脸识别
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Tip tip) {
        /* Do something */
        Log.d(TAG, "tip:" + tip.toString());
        jumpToVideoActivity(0);
//        Toast.makeText(getApplicationContext(), tip.getMessage(), Toast.LENGTH_SHORT).show();
    }


    /*
    * jump to ManagerMedicineActivity
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NurseBackEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        jumpToManagerMedicineActivity(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(HomePageActivity.this, VideoActivity.class), 1);
                return;
            }
            checkPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //VideoActivity return

        if (requestCode == Keys.VIDEO_REQUEST && resultCode == Keys.VIDEO_RESULT) {
            if (data != null) {
                Log.i(TAG, "VIDEO return data: " + data);
                /*String result = data.getExtras().getString(Keys.videoResult);
                if (remindDialog == null) {
                    remindDialog = new RemindDialog(this, result);
                }
                remindDialog.setTitle(result);
                remindDialog.show();*/
                return;
            }
        }
        //ManagerMedicineActivity return
        if (requestCode == Keys.CHAT_REQUEST && resultCode == Keys.CHAT_RESULT) {
            if (data != null) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.btn_video:
                checkPermission();
                break;*/

            case R.id.btn_chat:
                checkPermission();
                break;
//
//            case R.id.btn_diagnose:
//                jumpToDiagnoseActivity();
//                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items.
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void showTip(final String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static Context getContext(){
        return context;
    }

}

