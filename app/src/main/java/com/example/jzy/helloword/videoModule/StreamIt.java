package com.example.jzy.helloword.videoModule;

/**
 * Created by jzy on 1/18/17.
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.util.Base64;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import android.util.Log;

import com.example.jzy.helloword.event.ChangeEvent;
import com.example.jzy.helloword.event.MessageEvent;
import com.example.jzy.helloword.event.PatientBackEnvent;
import com.example.jzy.helloword.event.NurseBackEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import static android.util.Base64.DEFAULT;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;


public class StreamIt implements Camera.PreviewCallback {
    private String Url;
    private String userInfoURL;
    private int lastestTime;
    private int flag;

    public StreamIt(String serverURL,int flag,String userInfoURL) {
        EventBus.getDefault().register(this);
        this.Url = serverURL;
        this.flag = flag;
        this.userInfoURL = userInfoURL;
    }

    public void destroyInstance() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        if (seconds - lastestTime < 10) {
            Log.i("Sys", "seconds:" + seconds);
            Log.i("Sys", "lastestTime:" + lastestTime);
            return;
        }
        lastestTime = seconds;
        /*if(lastestTime == 50){
            lastestTime = 51;
        }*/
        if (lastestTime >= 50) {
            lastestTime = 0;
            return;
        }



        Size size = camera.getParameters().getPreviewSize();
        Camera.Parameters parameters = camera.getParameters();

        try {
            // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
            YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), size.width,
                    size.height, null);
            if (image != null) {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                        80, outstream);
                outstream.flush();
                // 启用线程将图像数据发送出去

                Thread th = new SendVideoThread(outstream, Url,flag,userInfoURL);
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

class SendVideoThread extends Thread {
    /*    private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;*/
    private static final int UPDATE = 1;
    private static final int SENDIDSTATUS = 0;
    private static final int PATIENT = 0;
    private static final int NURSE = 1;
    private static final int RESPOSE_SUCCESS = 1;
    private static final int RESPOSE_FAIL = 0;

    private ByteArrayOutputStream myoutputstream;
    private String Url;
    private String userInfoURL;
    private String Error = null;
    private int flag;
    URL url;
    BufferedReader reader = null;
//    JSONObject jsonObject;

    public SendVideoThread(ByteArrayOutputStream myoutputstream, String Url, int flag, String userInfoURL) {
        this.myoutputstream = myoutputstream;
        this.Url = Url;
        this.flag = flag;
        this.userInfoURL = userInfoURL;
        try {
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {


        // Send data
        try {


            /*
            * get respose from photo server
            * */
            //sleep(500);
            int AddUserID = 1;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("photo", Base64.encodeToString(myoutputstream.toByteArray(), DEFAULT));
            if(flag == UPDATE) {
                jsonObject.put("userID", AddUserID);
                jsonObject.put("userName", "MrCai");
            }
            //jsonObject.put("title",myoutputstream.toString());


            // Send POST data request
            String URL = Url;
            if(flag == UPDATE){
                URL = Url + "/update";
            }else if(flag == SENDIDSTATUS){
                URL = Url + "/sendIDStatus";
            }
            url = new URL(URL);
            Log.i("Sys", "URL:" + URL);
            URLConnection conn = url.openConnection();
//            HttpURLConnection conn= (HttpURLConnection) new URL(Url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            Log.i("Sys", "jsonObject:" + jsonObject.toString());
            wr.close();

            // Get the server response
            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
//            conn.disconnect();
//            Log.i("Sys", "TURN response: " + response);
            JSONObject responseJSON = new JSONObject(response);
            if(flag == SENDIDSTATUS) {
                int userID = responseJSON.getInt("userID");
                String userName = responseJSON.getString("userName");
                int status = responseJSON.getInt("status");
                int emojiID = responseJSON.getInt("emojiID");
                Log.i("Sys", "userID:" + userID);
                Log.i("Sys", "userName:" + userName);
                Log.i("Sys", "status:" + status);
                Log.i("Sys", "emojiID:" + emojiID);
                returnResult(userID,status,emojiID,userName);
            }else if (flag == UPDATE) {
                int ifSucc = responseJSON.getInt("ifSucc");
                Log.i("Sys", "ifSucc:" + ifSucc);
                returnSucc(ifSucc);
            }
        } catch (Exception ex) {
            Error = ex.getMessage();
            Log.e("ERROR", "create url false:" + Error);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }

        /*****************************************************/
    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void returnResult(int userID,int status,int emojiID,String userName){
        Log.i("Sys", "userID:" + userID);
        Log.i("Sys", "status:" + status);
        Log.i("Sys", "emojiID:" + emojiID);
        if(userID == -2 && status == -1 && emojiID == -1){
            Log.i("Sys", "come false emojiID:" + emojiID);
//            EventBus.getDefault().post(new PatientBackEnvent(userID,status,emojiID,userName));
            EventBus.getDefault().post(new MessageEvent("没有识别到脸，请对准镜头"));
        }else if(userID == -1 && status == 0){
            Log.i("Sys", "come userId emojiID:" + emojiID);
            EventBus.getDefault().post(new ChangeEvent("我好像不认识你,需要添加新用户吗？"));
        }else{
            Log.i("Sys", "come userID status emojiID:" + emojiID);
            /*
            * after get user id then query user information
            * */
            queryUserInfo(userID);
//            EventBus.getDefault().post(new PatientBackEnvent(userID,status,emojiID,userName));
        }

    }

    private void queryUserInfo(int userID) {
        try {
            /*
            * get user information from userInfo server
            * */

            String URL = userInfoURL;
            URL += "/QueryID";
            url = new URL(URL);
            Log.i("Sys", "URL:" + URL);
            JSONObject queryJson = new JSONObject();
            queryJson.put("user_id",userID);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(queryJson.toString());
            wr.flush();
            Log.i("Sys", "jsonObject:" + queryJson.toString());
            wr.close();

            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
            JSONObject responseJSON = new JSONObject(response);
            JSONObject result = responseJSON.getJSONObject("result");
            int isSuccess = result.getInt("isSuccess");
            String userName = result.getString("userName");
            int type = result.getInt("type");
            JSONArray patientNameGetArray = result.getJSONArray("patientNameArray");
            JSONArray patientIDGetArray = result.getJSONArray("patientIDArray");


            ArrayList<String> patientIDArray = new ArrayList<String>();
            ArrayList<String> patientNameArray = new ArrayList<String>();
            for(int i = 0; i < patientNameGetArray.length(); ++i) {
                patientNameArray.add(patientNameGetArray.getString(i));
                patientIDArray.add(patientIDGetArray.getString(i));
            }


            dealUserInfo(isSuccess,userName,type,userID,patientNameArray,patientIDArray);

        } catch (Exception ex) {
            Error = ex.getMessage();
            Log.e("ERROR", "create url false:" + Error);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }
    }

    private void dealUserInfo(int isSuccess
            ,String userName
            ,int type,int userID
            ,ArrayList<String> patientNameArray
            ,ArrayList<String> patientIDArray){
        Log.i("Sys", "isSuccess:" + isSuccess);
        Log.i("Sys", "userName:" + userName);
        Log.i("Sys", "type:" + type);
        if(isSuccess == RESPOSE_SUCCESS){
            if(type == PATIENT) {
                EventBus.getDefault().post(new PatientBackEnvent(userID, 1, 0, userName));
            }else if(type == NURSE){
                EventBus.getDefault().post(new NurseBackEvent(userID, userName,patientNameArray,patientIDArray));
            }
        }
    }

    private void returnSucc(int ifSucc) {
        if(ifSucc != 0){
            EventBus.getDefault().post(new PatientBackEnvent("success!"));
        }

    }
}
