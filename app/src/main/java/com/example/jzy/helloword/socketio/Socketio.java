package com.example.jzy.helloword.socketio;


import com.example.jzy.helloword.MyApplication;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by jzy on 11/7/17.
 */

public class Socketio {
    private Socket mSocket;
    static final int robotType = 0;
//    static final int roomNo = 10000;

    public Socketio()
    {
        try {
            mSocket = IO.socket("http://118.89.57.249:5000/");
        } catch (URISyntaxException e) {}
    }

    public void connect(){
        mSocket.connect();
    }

    public void attemptSend(String roomNo) {
        JSONObject sendParce = new JSONObject();
        try{
            sendParce.put("room", roomNo);
            sendParce.put("client_type",robotType);
            sendParce.put("user_name", MyApplication.getUserName());
        }catch(JSONException e){

        }
        mSocket.emit("join",sendParce.toString());
    }

    public void onDestroy(){
        mSocket.disconnect();
        mSocket.off("join");
    }

}
