package com.example.jzy.helloword;

import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.widget.RelativeLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;




public class Helloword extends AppCompatActivity {

    private static final String TAG = "CamTestActivity";
    Camera mCamera;
    Preview mPreview;
    CameraView mCameraView;
    Context ctx;
    int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_helloword);
        if(flag == 1) {
            mPreview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView));
            mPreview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }else {
            RelativeLayout topLayout = new RelativeLayout(this);
            setContentView(topLayout);
            mCamera = Camera.open(1);
            mCameraView = new CameraView(this, mCamera);
            topLayout.addView(mCameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mCameraView.startRecording();
        }
        //((FrameLayout) findViewById(R.id.layout)).addView(mPreview);
//        mPreview.setKeepScreenOn(true);

       /* mPreview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                mCamera = Camera.open(1);
                mCamera.startPreview();
                if(flag == 1) {
                    mPreview.setCamera(mCamera);
                }else {
                    mCameraView.setCamera(mCamera);
                    mCameraView.startRecording();
                }
            } catch (RuntimeException ex){
                Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(mCamera != null) {
            if(flag != 1) {
                mCameraView.stopRecording();
            }
            mCamera.stopPreview();
            if(flag == 1) {
                mPreview.setCamera(null);
            }else {
                mCameraView.setCamera(null);
            }
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }


}

