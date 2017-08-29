package com.example.jzy.helloword;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import com.example.jzy.helloword.entity.ChangeEvent;
import com.example.jzy.helloword.entity.MessageEvent;
import com.example.jzy.helloword.entity.Tip;
import com.example.jzy.helloword.entity.backEnvent;
import com.example.jzy.helloword.widget.RemindDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jzy on 8/2/17.
 */

public class VideoActivity extends AppCompatActivity {
    private static Context context;
    private static final String TAG = VideoActivity.class.getSimpleName();
    private Camera mCamera;
    private Preview mPreview;
    private CameraView mCameraView;
    private Context ctx;
    private String keyprefRoomServerUrl;
    private SharedPreferences sharedPref;
    private RemindDialog remindDialog;
    private int flag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_video);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventBus.getDefault().register(this);

        /**
         * get the parameter of URL
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        String roomURL = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));
        Bundle bundle = this.getIntent().getExtras();
        flag = bundle.getInt("flag");
        mPreview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView), roomURL,flag);
        mPreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if (null != mCameraView) {
            mCameraView.stopRecording();
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangeEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.getText());
        if (remindDialog == null) {
            remindDialog = new RemindDialog(this, event.getText());
        }
        remindDialog.setTitle(event.getText());
        remindDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        Log.d(TAG, "event:" + event.toString());
        showTip(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(backEnvent event) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showTip(final String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
