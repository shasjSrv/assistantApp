package com.example.jzy.helloword.videoModule;

/**
 * Created by jzy on 1/18/17.
 */

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Base64;
import android.util.Log;

import com.example.jzy.helloword.MyApplication;
import com.example.jzy.helloword.event.AddPatientEvent;
import com.example.jzy.helloword.event.AddPatientSuccEvent;
import com.example.jzy.helloword.event.MessageEvent;
import com.example.jzy.helloword.event.NurseBackEvent;
import com.example.jzy.helloword.event.PatientBackEnvent;
import com.example.jzy.helloword.managerMedicineModule.MedicineInfo;
import com.example.jzy.helloword.managerMedicineModule.Patient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import static android.util.Base64.DEFAULT;


public class StreamIt implements Camera.PreviewCallback {
    private String Url;
    private String userInfoURL;
    private int lastestTime;
    private int flag;

    public StreamIt(String serverURL,int flag,String userInfoURL) {
        Log.d("sys","1:come to set camera!");
//        EventBus.getDefault().register(this);
        this.Url = serverURL;
        this.flag = flag;
        this.userInfoURL = userInfoURL;
    }

    public void destroyInstance() {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        if (seconds - lastestTime < -8){
            lastestTime = -10;
        }
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
            lastestTime = -10;
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
            String AddUserID = MyApplication.getUserID();
            String name = MyApplication.getUserName();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("photo", Base64.encodeToString(myoutputstream.toByteArray(), DEFAULT));
            if(flag == UPDATE) {
                jsonObject.put("userID", AddUserID);
                jsonObject.put("userName", name);
            }

            // Send POST data request
            String URL = Url;
            if(flag == UPDATE){
                URL = Url + "/update";
            }else if(flag == SENDIDSTATUS){
                URL = Url + "/sendIDStatus";
            }
            url = new URL(URL);
            Log.d("Sys", "URL:" + URL);
            URLConnection conn = url.openConnection();
//            HttpURLConnection conn= (HttpURLConnection) new URL(Url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            Log.d("Sys", "jsonObject:" + jsonObject.toString());
            wr.close();

            // Get the server response
            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
//            conn.disconnect();
            JSONObject responseJSON = new JSONObject(response);
            if(flag == SENDIDSTATUS) {
                int userID = responseJSON.getInt("userID");
                String userName = responseJSON.getString("userName");
                int status = responseJSON.getInt("status");
                int emojiID = responseJSON.getInt("emojiID");
                Log.d("Sys", "userID:" + userID);
                Log.d("Sys", "userName:" + userName);
                Log.d("Sys", "status:" + status);
                Log.d("Sys", "emojiID:" + emojiID);
                returnResult(userID,status,emojiID,userName);
            }else if (flag == UPDATE) {
                int ifSucc = responseJSON.getInt("ifSucc");
                Log.d("Sys", "ifSucc:" + ifSucc);
                returnSucc(ifSucc, name);
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
    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void returnResult(int userID,int status,int emojiID,String userName){
        Log.d("Sys", "userID:" + userID);
        Log.d("Sys", "status:" + status);
        Log.d("Sys", "emojiID:" + emojiID);
        if(userID == -2 && status == -1 && emojiID == -1){
            Log.d("Sys", "come false emojiID:" + emojiID);
//            EventBus.getDefault().post(new PatientBackEnvent(userID,status,emojiID,userName));
            EventBus.getDefault().post(new MessageEvent("没有识别到脸，请对准镜头"));
        }else if(userID == -1 && status == 0){
            Log.d("Sys", "come userId emojiID:" + emojiID);
            EventBus.getDefault().post(new AddPatientEvent("我好像不认识你,需要添加新用户吗？"));
        }else{
            Log.d("Sys", "come userID status emojiID:" + emojiID);
            /*
            * after get user id then query user information
            * */
            queryUserInfo(userID);
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
            Log.d("Sys", "URL:" + URL);
            JSONObject queryJson = new JSONObject();
            queryJson.put("user_id",userID);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(queryJson.toString());
            wr.flush();
            Log.d("Sys", "jsonObject:" + queryJson.toString());
            wr.close();

            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
            JSONObject responseJSON = new JSONObject(response);
            JSONObject result = responseJSON.getJSONObject("result");
            int isSuccess = result.getInt("isSuccess");
            String userName = result.getString("userName");
            int type = result.getInt("type");

            MyApplication.setUserID(String.valueOf(userID));
            MyApplication.setUserName(userName);

            JSONArray patientNameGetArray = result.getJSONArray("patientNameArray");
            JSONArray patientIDGetArray = result.getJSONArray("patientIDArray");
            JSONArray patientRFIDGetArray = result.getJSONArray("patientRfIDArray");

            JSONArray medicineIDGetArray = result.getJSONArray("medicineIDArray");
            JSONArray medicineNameArray=result.getJSONArray("medicineNameArray");
            JSONArray medicineCountArray=result.getJSONArray("medicineCountArray");
            JSONArray medicineDosageArray=result.getJSONArray("medicineDosageArray");

            ArrayList<Patient> patientArray=new ArrayList<Patient>();
            int patientCount=patientNameGetArray.length();
            for(int i=0;i<patientCount;i++) {
                JSONArray m_id=medicineIDGetArray.getJSONArray(i);
                JSONArray m_name= medicineNameArray.getJSONArray(i);
                JSONArray m_count=medicineCountArray.getJSONArray(i);
                JSONArray m_dosage=medicineDosageArray.getJSONArray(i);
                int medicineCount=m_name.length();
                ArrayList<MedicineInfo> medicines=new ArrayList<MedicineInfo>();
                for(int j=0;j<medicineCount;j++){
                    MedicineInfo medicineInfo=new MedicineInfo(m_id.getString(j),m_name.getString(j),m_count.getString(j),m_dosage.getString(j));
                    medicines.add(medicineInfo);
                }
                Patient patient = new Patient(patientIDGetArray.getString(i),patientNameGetArray.getString(i),medicines,patientRFIDGetArray.getString(i));
                patientArray.add(patient);
            }

            dealUserInfo(isSuccess,userName,type,userID,patientArray);
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
            ,ArrayList<Patient> patientArray){
        Log.d("Sys", "isSuccess:" + isSuccess);
        Log.d("Sys", "userName:" + userName);
        Log.d("Sys", "type:" + type);
        if(isSuccess == RESPOSE_SUCCESS){
            if(type == PATIENT) {
                MyApplication.setUserType(PATIENT);
                EventBus.getDefault().post(new PatientBackEnvent(userID, 1, 0, userName));
            }else if(type == NURSE){
                MyApplication.setUserType(NURSE);
                EventBus.getDefault().post(new NurseBackEvent(userID, userName,patientArray));
            }
        }
    }

    private void returnSucc(int ifSucc, String name) {
        if(ifSucc != 0){
            EventBus.getDefault().post(new AddPatientSuccEvent(name));
        }

    }
}
