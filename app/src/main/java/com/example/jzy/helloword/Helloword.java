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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_helloword);
        mPreview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        mPreview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        /*RelativeLayout topLayout = new RelativeLayout(this);
        setContentView(topLayout);
        mCamera = Camera.open(1);
        mCameraView = new CameraView(this,mCamera);
        topLayout.addView(mCameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
 */


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
                mPreview.setCamera(mCamera);
                //mCameraView.setCamera(mCamera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            //mCameraView.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }


}

