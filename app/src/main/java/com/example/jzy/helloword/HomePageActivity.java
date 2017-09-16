package com.example.jzy.helloword;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jzy.helloword.event.AddEvent;
import com.example.jzy.helloword.event.NurseBackEvent;
import com.example.jzy.helloword.event.Tip;
import com.example.jzy.helloword.decisionModule.DecisionServices;
import com.example.jzy.helloword.videoModule.VideoActivity;
import com.example.jzy.helloword.videoModule.RemindDialog;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCClient;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCException;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyPrefBoxIP = getString(R.string.pref_box_ip_key);
        BoxIP = sharedPref.getString(
                keyPrefBoxIP, getString(R.string.pref_box_ip_default));

        new Thread(new Runnable(){
            @Override
            public void run() {
                XMLRPCClient client = new XMLRPCClient(BoxIP);
                try {
                    String hello = (String) client.call("Hello");
                    Log.i("XMLRPC Test", "result String hello = " + hello);
                }catch (XMLRPCException e){
                    Log.i("XMLRPC Test", "Error", e);
                }
            }
        }).start();



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

    }

    /**
     * 检查运行时权限（相机权限、文件写权限）
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            jumpToVideoActivity(0);
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
    private void jumpToChatActivity() {
        Intent intent = new Intent(HomePageActivity.this, ManagerMedicineActivity.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddEvent event) {
        /* Do something */
        Log.d(TAG, "flag:" + event.getFlag());
        jumpToVideoActivity(event.getFlag());
//        Toast.makeText(getApplicationContext(), tip.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NurseBackEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        jumpToChatActivity();
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
                jumpToChatActivity();
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

