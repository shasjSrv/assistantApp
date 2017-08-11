package com.example.jzy.helloword;

/**
 * Created by jzy on 1/16/17.
 */

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.ImageFormat;

class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    String serverURL;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(Context context,SurfaceView sv,String URL) {
        super(context);
        serverURL = URL;
        mSurfaceView = sv;
//        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

  private void stopPreviewAndFreeCamera() {

      if (mCamera != null) {
          mCamera.setPreviewCallback(null);

          // Call stopPreview() to stop updating the preview surface.
          mCamera.stopPreview();

          // Important: Call release() to release the camera for use by other
          // applications. Applications should release the camera immediately
          // during onPause() and re-open() it during onResume()).
          mCamera.release();

          mCamera = null;
      }
  }


    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }
        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            Camera.Parameters parameters = mCamera.getParameters();
            requestLayout();
            mCamera.setDisplayOrientation(90);
            parameters.setPreviewFpsRange(1, 1);
            try {

                parameters.setPictureFormat(ImageFormat.NV21);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setPreviewCallback(new StreamIt(serverURL));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
            mCamera.autoFocus(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if(mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

                requestLayout();

                mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }catch (IOException e) {
                Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
            }
        }
    }

}