package com.example.jzy.helloword;

/**
 * Created by jzy on 1/18/17.
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import android.util.Base64;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.util.Base64.DEFAULT;

import java.util.Calendar;
import java.util.Scanner;


public class StreamIt implements Camera.PreviewCallback{
    private String Url;
    private int lastestTime;

    public StreamIt(String serverURL) {
        this.Url = serverURL;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        if(seconds - lastestTime < 1) {
            Log.i("Sys", "seconds:" + seconds);
            Log.i("Sys", "lastestTime:" + lastestTime);
            return;
        }
        lastestTime = seconds;
        if(lastestTime > 58)
            lastestTime = 0;


        Size size = camera.getParameters().getPreviewSize();
        Camera.Parameters parameters = camera.getParameters();

        try {
            // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
            YuvImage image = new YuvImage(data,  parameters.getPreviewFormat(), size.width,
                    size.height, null);
            if (image != null) {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                        80, outstream);
                outstream.flush();
                // 启用线程将图像数据发送出去

                Thread th = new MyThread(outstream, Url);
                th.start();
              /*  try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {}*/
            }
        } catch (Exception ex) {
            Log.e("Sys", "Error:" + ex.getMessage());
        }
    }
}

class MyThread extends Thread {
/*    private byte byteBuffer[] = new byte[1024];
    private OutputStream outsocket;*/
    private ByteArrayOutputStream myoutputstream;
    private String Url;
    private String Error = null;
    URL url;
    BufferedReader reader=null;
    JSONObject jsonObject;

    public MyThread(ByteArrayOutputStream myoutputstream, String Url) {
        this.myoutputstream = myoutputstream;
        this.Url = Url;
        try {
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {


        // Send data
        try
        {

            //sleep(500);
            jsonObject = new JSONObject();
            jsonObject.put("photo",Base64.encodeToString(myoutputstream.toByteArray(), DEFAULT));
            //jsonObject.put("title",myoutputstream.toString());


            // Send POST data request
            url = new URL(Url);
            URLConnection conn = url.openConnection();
//            HttpURLConnection conn= (HttpURLConnection) new URL(Url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( jsonObject.toString());
            wr.flush();
            Log.i("Sys", "jsonObject:" + jsonObject.toString());
            wr.close();

            // Get the server response
           /* reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;*/

            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
//            conn.disconnect();
//            Log.i("Sys", "TURN response: " + response);
            JSONObject responseJSON = new JSONObject(response);
            int userID = responseJSON.getInt("userID");
            int status = responseJSON.getInt("status");
            int emojiID = responseJSON.getInt("emojiID");
            Log.i("Sys", "userID:" + userID);
            Log.i("Sys", "status:" + status);
            Log.i("Sys", "emojiID:" + emojiID);
           /* JSONObject task = responseJSON.getJSONObject("task");
            int id = task.getInt("id");*/
//            Log.i("Sys", "taskID:" + id);

//            JSONArray tasks = responseJSON.getJSONArray("task");
//            for (int i = 0; i < tasks.length(); ++i) {
//                JSONObject task = tasks.getJSONObject(i);
//                int id = task.getInt("id");
//                Log.i("Sys", "taskID:" + id);
//            }

        }
        catch(Exception ex)
        {
            Error = ex.getMessage();
            Log.e("ERROR", "create url false:" + Error);
        }
        finally
        {
            try
            {
                reader.close();
            }

            catch(Exception ex) {}
        }

        /*****************************************************/
    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
