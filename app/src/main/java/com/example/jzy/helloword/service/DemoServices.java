package com.example.jzy.helloword.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.jzy.helloword.HomePageActivity;
import com.example.jzy.helloword.entity.AddEvent;
import com.example.jzy.helloword.entity.AnswerEvent;
import com.example.jzy.helloword.entity.Tip;
import com.example.jzy.helloword.entity.BackEnvent;
import com.example.jzy.helloword.util.HandleResult;
import com.example.jzy.helloword.util.MyResult;
import com.example.jzy.helloword.util.MySpeechUnderstander;
import com.example.jzy.helloword.util.TTS;
import com.example.jzy.helloword.util.Waker;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jzy on 8/7/17.
 */

public class DemoServices extends Service {

    private int count;
    String serverURL = "https://video.moevis.cc:8888/chat";
    private final static String TAG = "All Demo with face";
    private final static int ANSWERMODE = 1;
    private final static int QUESTIONMODE = 0;
    private final static int ANSWERTWOMODE = 2;
    private final static int WAKERSTATU = 0;
    private final static int UNDERDANDSTATU = 1;
    static final String WELCOME = "你好，我是小易，请问有什么可以帮到你的";
    private String name = "";
    private int lastestTime;

    // Waker
    private Waker waker;

    // TTS
    private TTS mTts;
    // Understander
    private MySpeechUnderstander mSpeechUnderstander;

    private MediaPlayer mp;

    private boolean isPlaying, isRecording;
    private String answerText;
    private int flag = QUESTIONMODE;
    private int IsWaker = UNDERDANDSTATU;


    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            isPlaying = true;
        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
//            switch(count){
//                case 2: iv.setImageResource(R.drawable.face2); count++; break;
//                case 3: iv.setImageResource(R.drawable.face3); count++; break;
//                case 4: iv.setImageResource(R.drawable.face4); count=2; break;
//            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                isPlaying = false;
                // iv.setImageResource(R.drawable.face2);
                if(IsWaker == UNDERDANDSTATU) {
                    mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                }
            } else {
                HomePageActivity.showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    private SpeechUnderstanderListener speechUnderstandListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {

                IsWaker = UNDERDANDSTATU;
                // 显示
                String jsonReturn = result.getResultString();
                Log.e(TAG, "result:" + jsonReturn);
                String result1 = null;
                String text = null;
//                int service = HandleResult.whatService(jsonReturn);
               /* if(service==HandleResult.WEATHER){
                    answerText  = HandleResult.parseWeather(jsonReturn,result1,text);
                    if(answerText != null)
                        mTts.startSpeaking(answerText,mTtsListener);
                }
                if(service==HandleResult.MUSIC){
                    try {
                        playMusic(jsonReturn);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/
//                if(service==HandleResult.ANSWER){
                MyResult myResult = HandleResult.parseAnswer(jsonReturn, result1, text/*,cli*/);
//                answerText = HandleResult.parseAnswer(jsonReturn, result1, text/*,cli*/);
               /* answerText = myResult.getResult();
                if (answerText != null) {
                    mTts.startSpeaking(answerText, mTtsListener);
                    changeActicityCondition(text);
                }else
                    mTts.startSpeaking("识别结果不正确。", mTtsListener);*/



                ;
//                }
                dealResult(myResult);

                /*if(myResult == null){
                    Log.i("Sys","myResult is null");
                }
                if(myResult == null) {
                    if (flag == QUESTIONMODE) {
                        mTts.startSpeaking("对不起，我没有听清楚。", mTtsListener);
                    }
                }else {
                    if(flag == QUESTIONMODE) {
                        Thread th = new SendChatThread(serverURL, myResult.getText());
                        th.start();
                    }else if(flag == ANSWERMODE){
//                        mTts.startSpeaking("嗯",mTtsListener);
                        Calendar c = Calendar.getInstance();
                        lastestTime = c.get(Calendar.MILLISECOND);
                        while(true) {
                            c = Calendar.getInstance();
                            int seconds = c.get(Calendar.MILLISECOND);
                            if(lastestTime > 955){
                                lastestTime -= 1000;
                                seconds  -= 1000;
                            }
                            if (seconds - lastestTime < 10) {
                                Log.i("Sys", "seconds:" + seconds);
                                Log.i("Sys", "lastestTime:" + lastestTime);
                                continue;
                            }
                            break;
                        }
                        mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                        flag = ANSWERTWOMODE;
                    }else if(flag == ANSWERTWOMODE){
                        mTts.startSpeaking("嗯",mTtsListener);
                        flag = QUESTIONMODE;
                    }
                }*/
                //TODO 语音理解
            } else {
                HomePageActivity.showTip("识别结果不正确。");
            }

        }
        private void changeActicityCondition(String text){
            int index = text.indexOf("增加用户");
            Log.i(TAG, "index:" + index);
            if(index != -1){
                EventBus.getDefault().post(new AddEvent(text));
            }
        }

        private void dealResult(MyResult myResult){
            if(myResult == null){
                Log.i("Sys","myResult is null");
            }
            if(myResult == null) {
                if (flag == QUESTIONMODE) {
                    mTts.startSpeaking("对不起，我没有听清楚。", mTtsListener);
                }
            }else {
                if(flag == QUESTIONMODE) {
                    Thread th = new SendChatThread(serverURL, myResult.getText());
                    th.start();
                }else if(flag == ANSWERMODE){
//                        mTts.startSpeaking("嗯",mTtsListener);
                    Calendar c = Calendar.getInstance();
                    lastestTime = c.get(Calendar.MILLISECOND);
                    while(true) {
                        c = Calendar.getInstance();
                        int seconds = c.get(Calendar.MILLISECOND);
                        if(lastestTime > 955){
                            lastestTime -= 1000;
                            seconds  -= 1000;
                        }
                        if (seconds - lastestTime < 10) {
                            Log.i("Sys", "seconds:" + seconds);
                            Log.i("Sys", "lastestTime:" + lastestTime);
                            continue;
                        }
                        break;
                    }
                    mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                    flag = ANSWERTWOMODE;
                }else if(flag == ANSWERTWOMODE){
                    mTts.startSpeaking("嗯",mTtsListener);
                    flag = QUESTIONMODE;
                }
            }
        }

        private String drainStream(InputStream in) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }


        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            HomePageActivity.showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length + "");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            isRecording = false;
            /**
             * @TODO MAKE THE BUTTON INVISIBLE
             */

            HomePageActivity.showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            isRecording = true;
            HomePageActivity.showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // 14002
            Log.e(TAG, "YU YIN ERROR");
            HomePageActivity.showTip(error.getPlainDescription(true));
            answerText = WELCOME;
            waker.startListening(mWakeuperListener);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.e(TAG, "onResult");
            VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
            if (mIvw != null) {
                mIvw.destroy();
            }
            mTts.startSpeaking(answerText, mTtsListener);
            IsWaker = WAKERSTATU;

            changeActicityCondition(answerText);

            //VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
            //mIvw.stopListening();
        }

        private void changeActicityCondition(String text){
            int index = text.indexOf("你好");
            Log.i(TAG, "index:" + index);
            if(text.indexOf("你好") != -1) {
                EventBus.getDefault().post(new Tip(text));
            }
        }

        @Override
        public void onError(SpeechError error) {
            HomePageActivity.showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {

        }

        @Override
        public void onVolumeChanged(int volume) {
            HomePageActivity.showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + volume);
        }
    };// end of wakeruperListener


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "before new client");
//        cli = new Client(ChatActivity.ServerIP,ChatActivity.ServerPort);
        Log.d(TAG, "before new MediaPlayer");
        mp = new MediaPlayer();
        Log.d(TAG, "Begin");
        //初始化唤醒对象
        waker = new Waker();
        waker.startListening(mWakeuperListener);
        // 初始化合成对象
        mTts = new TTS();
        // 初始化语音语义理解对象
        mSpeechUnderstander = new MySpeechUnderstander();
        isPlaying = false;
        isRecording = false;
        answerText = WELCOME;
        count = 2;
        EventBus.getDefault().register(this);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e(ChatActivity.TAG,"the service start");
        Log.i(HomePageActivity.TAG, "the service start");
        HomePageActivity.showTip("the service start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        waker.destory();
        mTts.destory();
        mSpeechUnderstander.destory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackEnvent event) {
        //Do something
        waker.stopListening();
        name = event.toString();
        mTts.startSpeaking("你好" + event.toString() + "今天拿药吃了吗", mTtsListener);


        mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
        flag = ANSWERMODE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerEvent event) {
        //Do something
//        waker.stopListening();
        mTts.startSpeaking(event.getText(), mTtsListener);
        flag = QUESTIONMODE;
    }

    private void playMusic(String result) throws JSONException {
        String url = null;
        url = HandleResult.parseMusic(result);
        Log.e(TAG, url);
        if (url == "null") {
            String answerText = "你想听谁唱的呀？";
            mTts.startSpeaking(answerText, mTtsListener);

            mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
        } else {
            initMediaPlayer(url);
            mp.start();
        }


    }

    private void initMediaPlayer(String url) {

        //File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");
        try {
            //MediaPlayer mp = MediaPlayer.create(this, R.raw.music1);
            mp.setDataSource(url);
            mp.prepare();

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

class SendChatThread extends Thread {
    /*    private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;*/
//    private ByteArrayOutputStream myoutputstream;
    private String Url;
    private String context;
    private String Error = null;
    URL url;
    BufferedReader reader = null;
    JSONObject jsonObject;
    private AssetManager assets;

    public SendChatThread(String Url, String context) {
        this.Url = Url;
        this.context = context;
        /*try {
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public void run() {


        // Send data
        try {



            jsonObject = new JSONObject();
//            jsonObject.put("photo", Base64.encodeToString(myoutputstream.toByteArray(), DEFAULT));
            jsonObject.put("user_id", 1);
            jsonObject.put("content", context);
            //jsonObject.put("title",myoutputstream.toString());


            // Send POST data request
            url = new URL(Url);
//            URLConnection conn = url.openConnection();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            HttpsURLConnection conn= (HttpsURLConnection) new URL(Url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
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
            JSONObject resultText = responseJSON.getJSONObject("result");
            String text = resultText.getString("text");
//            int resultType = responseJSON.getInt("type");
            Log.i("Sys", "text:" + text);
            EventBus.getDefault().post(new AnswerEvent(text));
//            Log.i("Sys", "resultType:" + resultType);



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
    private class MyHostnameVerifier implements HostnameVerifier {

        @Override

        public boolean verify(String hostname, SSLSession session) {
// TODO Auto-generated method stub

            return true;
        }

    }

    private class MyTrustManager implements X509TrustManager {

        @Override

        public void checkClientTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
// TODO Auto-generated method stub


        }
        @Override

        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
// TODO Auto-generated method stub

        }
        @Override

        public X509Certificate[] getAcceptedIssuers() {

// TODO Auto-generated method stub

            return null;
        }
    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}

