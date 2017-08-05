package com.example.jzy.helloword;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dd.CircularProgressButton;

import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;


public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomePageActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST = 1;
    private Button btnVideo, btnChat, btnDiagnose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_homepage);
        init();
    }

    private void init() {
        btnVideo = (Button) findViewById(R.id.btn_video);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnDiagnose = (Button) findViewById(R.id.btn_diagnose);
        btnVideo.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnDiagnose.setOnClickListener(this);
    }

    /**
     * 检查运行时权限（相机权限、文件写权限）
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            jumpToVideoActivity();
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }

    /**
     * 跳转至VideoActivity
     */
    private void jumpToVideoActivity() {
        Intent intent = new Intent(HomePageActivity.this, VideoActivity.class);
        startActivityForResult(intent, Keys.VIDEO_REQUEST);
    }

    /**
     * 跳转至ChatActivity
     */
    private void jumpToChatActivity() {
        Intent intent = new Intent(HomePageActivity.this, ChatActivity.class);
        startActivityForResult(intent, Keys.CHAT_REQUEST);
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
                String result = data.getExtras().getString(Keys.videoResult);
                return;
            }
        }
        //ChatActivity return
        if (requestCode == Keys.CHAT_REQUEST && resultCode == Keys.CHAT_RESULT) {
            if (data != null) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video:
                checkPermission();
                break;

            case R.id.btn_chat:
                jumpToChatActivity();
                break;

            case R.id.btn_diagnose:

                break;

            default:
                break;
        }
    }
}

