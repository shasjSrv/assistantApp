package com.example.jzy.helloword.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.example.jzy.helloword.ChatActivity;
import com.example.jzy.helloword.util.HandleResult;
import com.example.jzy.helloword.util.MySpeechUnderstander;
import com.example.jzy.helloword.util.TTS;
import com.example.jzy.helloword.util.Waker;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jzy on 8/7/17.
 */

public class DemoServices extends Service {

    private int count;
    private final static String TAG = "All Demo with face";
    static final String WELCOME = "你好，我是护士姐姐，请问有什么可以帮到你的";
    // Waker
    private Waker waker;

    // TTS
    private TTS mTts;
    // Understander
    private MySpeechUnderstander mSpeechUnderstander;

    private MediaPlayer mp ;

    private boolean isPlaying, isRecording;
    private String answerText;


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
                mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
            } else {
                ChatActivity.showTip(error.getPlainDescription(true));
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
                // 显示
                String jsonReturn = result.getResultString();
                Log.e(TAG, "result:"+jsonReturn);
                String result1=null;
                String text=null;
                int service= HandleResult.whatService(jsonReturn);
                if(service==HandleResult.WEATHER){
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
                }
                if(service==HandleResult.ANSWER){
                    answerText = HandleResult.parseAnswer(jsonReturn,result1,text/*,cli*/);
                    if(answerText != null)
                        mTts.startSpeaking(answerText, mTtsListener);
                }
                //TODO 语音理解
            } else {
                ChatActivity.showTip("识别结果不正确。");
            }

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            ChatActivity.showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length + "");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            isRecording = false;
            /**
             * @TODO MAKE THE BUTTON INVISIBLE
             */

            ChatActivity.showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            isRecording = true;
            ChatActivity.showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // 14002
            Log.e(TAG, "YU YIN ERROR");
            ChatActivity.showTip(error.getPlainDescription(true));
            answerText=WELCOME;
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
            mTts.startSpeaking(answerText,mTtsListener);
            //VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
            //mIvw.stopListening();
        }

        @Override
        public void onError(SpeechError error) {
            ChatActivity.showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {

        }

        @Override
        public void onVolumeChanged(int volume) {
            ChatActivity.showTip("当前正在说话，音量大小：" + volume);
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
        mTts =  new TTS();
        // 初始化语音语义理解对象
        mSpeechUnderstander = new MySpeechUnderstander();
        isPlaying = false;
        isRecording = false;
        answerText = WELCOME;
        count=2;


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e(ChatActivity.TAG,"the service start");
        Log.i(ChatActivity.TAG,"the service start");
        ChatActivity.showTip("the service start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        waker.destory();
        mTts.destory();
        mSpeechUnderstander.destory();
    }

    private void playMusic(String result) throws JSONException {
        String url = null;
        url=HandleResult.parseMusic(result);
        Log.e(TAG, url);
        if(url=="null"){
            String answerText="你想听谁唱的呀？";
            mTts.startSpeaking(answerText,mTtsListener);

            mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
        }else{
            initMediaPlayer(url);
            mp.start();
        }


    }

    private void initMediaPlayer(String url) {

        //File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");
        try {
            //MediaPlayer mp = MediaPlayer.create(this, R.raw.music1);
            mp.setDataSource( url);
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
