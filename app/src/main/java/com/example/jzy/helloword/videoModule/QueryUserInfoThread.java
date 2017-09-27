package com.example.jzy.helloword.videoModule;

import android.app.Notification;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by jzy on 9/25/17.
 */

public class QueryUserInfoThread extends Thread {
    //    private String Url;
    private String userInfoURL;
    private String Error = null;
    private String userID;
    private Handler handler;
    //    private int flag;
    URL url;
    BufferedReader reader = null;

    public QueryUserInfoThread(String userInfoURL, String userID, Handler handler) {
        this.userInfoURL = userInfoURL;
        this.userID = userID;
        this.handler = handler;
    }

    public void run() {
        try {
            // sleep(300);
            // dialog.show();
            //模拟数据
          /*  JSONObject resulttest = new JSONObject();
            resulttest.put("username", "艾米");
            resulttest.put("age", 18);
            resulttest.put("gender", "女");
            resulttest.put("roomNo", 615);
            resulttest.put("berthNo", 02);
            resulttest.put("diagnoseId",userID); */




            String URL = userInfoURL;
            URL += "/CheckUpdateCondition";
            url = new URL(URL);
            Log.i("Syss", "URL:" + URL);
            JSONObject queryJson = new JSONObject();
            queryJson.put("user_id", userID);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(queryJson.toString());
            wr.flush();
            Log.i("Syss", "jsonObject:" + queryJson.toString());
            wr.close();

            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
            JSONObject responseJSON = new JSONObject(response);
            JSONObject result = responseJSON.getJSONObject("result");

            int isSuccess = result.getInt("isSuccess");
            String userName = result.getString("userName");
           /*  int age = result.getInt("age");
            String gender = result.getString("gender");
            String rfid = result.getString("rfid");
            int roomNo = result.getInt("roomNo");
            int berthNo = result.getInt("berthNo");*/

            Message msg = new Message();
            msg.what = 1;
            msg.obj = result;
            handler.sendMessage(msg);

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

    private void dealRespose() {

    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
