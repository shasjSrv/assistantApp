package com.example.jzy.helloword.decisionModule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jzy.helloword.HomePageActivity;
import com.example.jzy.helloword.MyApplication;
import com.example.jzy.helloword.R;
import com.example.jzy.helloword.event.AddPatientSuccEvent;
import com.example.jzy.helloword.event.AnswerEvent;
import com.example.jzy.helloword.event.BackPressedEvent;
import com.example.jzy.helloword.event.CallDoctorEvent;
import com.example.jzy.helloword.event.NotifyEvent;
import com.example.jzy.helloword.event.PatientBackEnvent;
import com.example.jzy.helloword.event.NurseBackEvent;
import com.example.jzy.helloword.event.SleepEvent;
import com.example.jzy.helloword.event.Tip;
import com.example.jzy.helloword.voiceModule.HandleResult;
import com.example.jzy.helloword.voiceModule.MyResult;
import com.example.jzy.helloword.voiceModule.MySpeechUnderstander;
import com.example.jzy.helloword.voiceModule.TTS;
import com.example.jzy.helloword.voiceModule.Waker;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCClient;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCException;
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
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jzy on 8/7/17.
 */

public class DecisionServices extends Service {


    private final static String serverURL = "https://video.moevis.cc:8888/chat";
    private final static String TAG = "All Demo with face";
    //    private static final String WELCOME = "你好，我是小易，请问有什么可以帮到你的";
    private String name = "";
    // Waker
    private Waker waker;

    // TTS
    private TTS mTts;
    // Understander
    private MySpeechUnderstander mSpeechUnderstander;

    //StateMachine
    /*control the state of deciseion*/
    public ControlStateMachine mCsm;


    private MediaPlayer mp;

    private boolean isPlaying;
    private String answerText;
    private MyResult myAnswerResult;


    private class ControlStateMachine extends StateMachine {
//        private final String TAG1 = MyStateMachine.class.getSimpleName();

        private final static int MSG_WAKE_UP = 1;
        private final static int MSG_DETECTION_PATIENT_SUCCESS = 2;
        private final static int MSG_DETECTION_FALSE = 3;
        private final static int MSG_ANSWER = 4;
        private final static int MSG_TEMPLATE = 5;
        private final static int MSG_QUESTION = 6;
        private final static int MSG_TTS_COMPLETE = 7;
        private final static int MSG_BACK_SLEEP = 8;
        private final static int MSG_DETECTION_NURSE_SUCCESS = 9;
        private final static int MSG_DETECTION_NURSE_FALSE = 10;
        private int latestState = 0;


        public ControlStateMachine() {
            super(TAG);  // 调用StateMachine(String name)构造状态机
            Log.d(TAG, "ctor E");
            addState(mDefaulteState, null);  // 加入默认状态作为父状态，子状态处理不了的命令可以交给它来报错
            addState(mSleepState, mDefaulteState);
            addState(mDetectState, mDefaulteState);
            addState(mAnswerState, mDefaulteState);
            addState(mTempState, mDefaulteState);
            addState(mQuestionState, mDefaulteState);
            addState(mTtsCompleteState, mDefaulteState);
            addState(mPutMedicineState, mDefaulteState);
            addState(mAddPatientState, mDefaulteState);
            setInitialState(mSleepState); // sleep状态为初始状态
            Log.d(TAG, "ctor X");
            start(); // 状态机进入初始状态等候外界的命令
        }

        public void wakeup() {
            sendMessage(MSG_WAKE_UP);
        }

        public void detectPatientSuccess() {
            sendMessage(MSG_DETECTION_PATIENT_SUCCESS);
        }

        public void detectNurseSuccess() {
            sendMessage(MSG_DETECTION_NURSE_SUCCESS);
        }

        public void changeToTempMode() {
            sendMessage(MSG_TEMPLATE);
        }

        public void changeToQuestionMode() {
            sendMessage(MSG_QUESTION);
        }

        public void ttsComplete() {
            sendMessage(MSG_TTS_COMPLETE);
        }

        public void backToSleep() {
            sendMessage(MSG_BACK_SLEEP);
        }

        public void detectFalse() {
            sendMessage(MSG_DETECTION_FALSE);
        }

        private boolean checkResult(MyResult myResult) {
            if (myResult == null) {
                Log.i("Sys", "myResult is null");
                return false;
            }
            return true;
        }

        private void sendTextToChatServer(String text){
            Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread th, Throwable ex) {
                    Log.e("ERROR","Uncaught exception: " + ex);
                    mTts.startSpeaking(Corpus.NetWorkBad, mTtsListener);
                    backToSleep();
                }
            };
            Thread th = new SendChatThread(serverURL, text);
            th.setUncaughtExceptionHandler(h);
            th.start();
        }
        //deal with state of TTS in different states
        public void dealTTSState() {
            switch (latestState) {
                case MSG_ANSWER:
                    break;
                case MSG_QUESTION:
                    if(isNeedDoctor(myAnswerResult.getText()))
                    {
                        callDoctor();
                        break;
                    }
                    sendTextToChatServer(myAnswerResult.getText());
                    break;
                case MSG_TEMPLATE:
                    mTts.startSpeaking("嗯", mTtsListener);
                    break;
                default:
                    return;
            }

        }

        //判断用户是否想要呼叫医生
        public boolean isNeedDoctor(String inputStr){
            boolean isMatch=false;
            String pattern1=".*不(需要|要|用).*医生.*";
            String pattern2=".*(请|找|叫|呼叫|喊|需要|要).*医生.*";

            boolean isMatchNo= Pattern.matches(pattern1,inputStr);
            if(!isMatchNo){
                isMatch=Pattern.matches(pattern2,inputStr);
            }
            return isMatch;
        }
        //呼叫医生
        public void callDoctor(){
            this.backToSleep();
            EventBus.getDefault().post(new CallDoctorEvent());
        }

        public void startSpeaking() {
            mTts.startSpeaking(Corpus.QueryAddPatient, mTtsListener);
        }


        private State mDefaulteState = new DefaultState();

        class DefaultState extends State {

            @Override
            public boolean processMessage(Message msg) {
                return true;
            }
        }

        private State mSleepState = new SleepState();

        class SleepState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                latestState = MSG_WAKE_UP;
                waker.startListening(mWakeuperListener);
                EventBus.getDefault().post(new SleepEvent());

                if(MyApplication.getAvailableBoxNum()==4)
                {
                    EventBus.getDefault().post(new CallDoctorEvent());
                    MyApplication.setAvailableBoxNum(0);
                }

            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_WAKE_UP:
                        transitionTo(mDetectState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mDetectState = new DetectState();

        class DetectState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                EventBus.getDefault().post(new Tip(""));
//                changeToAnswerMode();
            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_DETECTION_PATIENT_SUCCESS:

                        transitionTo(mAnswerState);
                        break;
                    case MSG_DETECTION_NURSE_SUCCESS:

                        transitionTo(mPutMedicineState);
                        break;
                    case MSG_DETECTION_FALSE:
                        transitionTo(mAddPatientState);
                    case MSG_BACK_SLEEP:
                        transitionTo(mSleepState);
                        break;
                    default:
                        return false;
                }
                return true;
            }



        }

        private State mAnswerState = new AnswerState();

        class AnswerState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                latestState = MSG_ANSWER;
                Log.i(TAG, "AnswerText: " + name);
                queryBoxAvaliable();
                sendTextToChatServer("");
//                waker.stopListening();
//                mTts.startSpeaking(name, mTtsListener);
            }
            void queryBoxAvaliable(){

                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                        System.out.println("Uncaught exception: " + ex);
                    }

                };
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            String keyPrefBoxIP=getString(R.string.pref_box_ip_key);
                            String medicineBoxIP = sharedPref.getString(
                                    keyPrefBoxIP, getString(R.string.pref_box_ip_default));

                            XMLRPCClient client = new XMLRPCClient(medicineBoxIP);
                            //int boxAvalib = (Integer) client.call("QueryAvailable");
                            MyApplication.setAvailableBoxNum((Integer) client.call("QueryAvailable"));
                            Log.i("XMLRPC Test", "QueryMedicine queryState  = " + MyApplication.getAvailableBoxNum());

                        } catch (XMLRPCException e) {
                            Log.i("XMLRPC Test", "Error", e);
                            throw new RuntimeException("The network is too bad!");
                        }
                    }
                });
                t.setUncaughtExceptionHandler(h);
                t.start();
            }
            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TEMPLATE:
                        transitionTo(mTempState);
                        break;
                    case MSG_TTS_COMPLETE:
                        transitionTo(mTtsCompleteState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mPutMedicineState = new PutMedicineState();

        class PutMedicineState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                latestState = MSG_DETECTION_NURSE_SUCCESS;
                Log.i(TAG, "AnswerText: " + name);
                waker.stopListening();
                mTts.startSpeaking(name, mTtsListener);
//                transitionTo(mSleepState);
            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_BACK_SLEEP:
                        transitionTo(mSleepState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mAddPatientState = new AddPatientState();

        class AddPatientState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());

                waker.stopListening();
                if (latestState != MSG_TTS_COMPLETE) {
                    mCsm.startSpeaking();
                }
                latestState = MSG_DETECTION_FALSE;
            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_BACK_SLEEP:
                        transitionTo(mSleepState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mTempState = new TempState();

        class TempState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                latestState = MSG_TEMPLATE;
                mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_QUESTION:
                        transitionTo(mQuestionState);
                        break;
                    case MSG_TTS_COMPLETE:
                        transitionTo(mTtsCompleteState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mQuestionState = new QuestionState();

        class QuestionState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                latestState = MSG_QUESTION;
                mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
//                notifyUI(getName());
            }

            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_BACK_SLEEP:
                        transitionTo(mSleepState);
                        break;
                    case MSG_TTS_COMPLETE:
                        transitionTo(mTtsCompleteState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

        private State mTtsCompleteState = new TtsCompleteState();

        class TtsCompleteState extends State {
            @Override
            public void enter() {
                Log.i(TAG, "enter " + getName());
                switch (latestState) {
                    case MSG_QUESTION:

//                        mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                        changeToQuestionMode();
                        break;
                    case MSG_TEMPLATE:
//                        mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                        changeToQuestionMode();
                        break;
                    case MSG_WAKE_UP:
                        break;
                    case MSG_ANSWER:
//                        mSpeechUnderstander.startUnderStanding(speechUnderstandListener);
                        changeToQuestionMode();
//                        changeToTempMode();
                        break;
                    case MSG_DETECTION_FALSE:
                        detectFalse();
                        break;
                    default:
                }
                latestState = MSG_TTS_COMPLETE;
            }

            @Override

            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case MSG_QUESTION:
                        transitionTo(mQuestionState);
                        break;
                    case MSG_TEMPLATE:
                        transitionTo(mTempState);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        }

    }

    ;


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

        }

        @Override
        public void onCompleted(SpeechError error) {
            Log.i(TAG, "TTS onCompleted thread ID: " + android.os.Process.myTid());
            if (error == null) {
                isPlaying = false;
                mCsm.ttsComplete();

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

//                IsWaker = UNDERDANDSTATU;
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
                myAnswerResult = HandleResult.parseAnswer(jsonReturn, result1, text/*,cli*/);
                mCsm.dealTTSState();

                //TODO 语音理解
            } else {
                HomePageActivity.showTip("识别结果不正确。");
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
            /**
             * @TODO MAKE THE BUTTON INVISIBLE
             */

            HomePageActivity.showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            HomePageActivity.showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // 14002
            Log.e(TAG, "YU YIN ERROR");
            HomePageActivity.showTip(error.getPlainDescription(true));
            mCsm.backToSleep();
            answerText = Corpus.Wellcom;
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
            Log.i(TAG, "before TTS onCompleted thread ID: " + android.os.Process.myTid());
            mTts.startSpeaking(answerText, mTtsListener);
//            IsWaker = WAKERSTATU;
            mCsm.wakeup();
//            changeActicityCondition(answerText);

            //VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
            //mIvw.stopListening();
        }

        private void changeActicityCondition(String text) {
            int index = text.indexOf("你好");
            Log.i(TAG, "index:" + index);
            if (text.indexOf("你好") != -1) {
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
        Log.i(TAG, "Start thread ID: " + android.os.Process.myTid());

        Log.d(TAG, "before new client");
//        cli = new Client(ManagerMedicineActivity.ServerIP,ManagerMedicineActivity.ServerPort);
        Log.d(TAG, "before new MediaPlayer");
        mp = new MediaPlayer();
        Log.d(TAG, "Begin");
        //初始化唤醒对象
        waker = new Waker();
//        waker.startListening(mWakeuperListener);
        // 初始化合成对象
        mTts = new TTS();
        // 初始化语音语义理解对象
        mSpeechUnderstander = new MySpeechUnderstander();
        myAnswerResult = new MyResult("", "");

        mCsm = new ControlStateMachine();

        isPlaying = false;
        answerText = Corpus.Wellcom;
        EventBus.getDefault().register(this);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e(ManagerMedicineActivity.TAG,"the service start");
        Log.i(HomePageActivity.TAG, "the service start");
        HomePageActivity.showTip("the service start");

        String command = intent.getStringExtra("command");
        if ("alarm".equals(command)) {
            alarmRemind();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void alarmRemind() {
        Context context = MyApplication.getContext();
        Toast.makeText(context, "run alarmremind", Toast.LENGTH_SHORT).show();
        //Log.d("alarm","run alarmremind");
        waker.stopListening();
        mTts.startSpeaking(Corpus.AlarmRemind, mTtsListener);

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
    public void onMessageEvent(AddPatientSuccEvent event) {
        //Do something
        name = "你好," + event.getName() + ",你的照片已经添加成功";
        mTts.startSpeaking(name, mTtsListener);
        mCsm.backToSleep();
    }

    /*
    * notify DecisionService change state to detectPatientSuccess
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PatientBackEnvent event) {
        //Do something
        name = "你好" + event.toString() + "今天拿药吃了吗";
        mCsm.detectPatientSuccess();
    }

    /*
    * get the result from chat server and speak to client(patient or nurse)
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerEvent event) {
        //Do something
//        waker.stopListening();
        mTts.startSpeaking(event.getText(), mTtsListener);
    }

    /*
    * return from p2p and speak to patient
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotifyEvent event) {
        //Do something
//        waker.stopListening();

        mTts.startSpeaking("请大家过来拿药", mTtsListener);
    }


    /*
    * notify DecisionService change state to detectNurseSuccess
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NurseBackEvent event) {
        //Do something
        name = "你好" + event.toString() + "你要为以下几位病人放药";
        mCsm.detectNurseSuccess();
    }

    /*
    * tell patient to entern call button and prepare to call doctor via video
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallDoctorEvent event){
        mTts.startSpeaking(Corpus.CallDoctor, mTtsListener);
    }

    /*
    * notify DecisionService change state to sleepState
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackPressedEvent event) {
        //Do something
        mCsm.backToSleep();
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

class SendChatThread extends Thread implements Runnable{
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
            jsonObject.put("user_id", MyApplication.getUserID());
            jsonObject.put("content", context);
            // Send POST data request
            url = new URL(Url);
//            URLConnection conn = url.openConnection();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(Url).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            Log.i("Sys", "jsonObject:" + jsonObject.toString());
            wr.close();

            InputStream responseStream = conn.getInputStream();
            String response = drainStream(responseStream);
            JSONObject responseJSON = new JSONObject(response);
            JSONObject resultText = responseJSON.getJSONObject("result");
            String text = resultText.getString("text");
            Log.i("Sys", "text:" + text);
            EventBus.getDefault().post(new AnswerEvent(text));
        } catch (Exception ex) {
            Error = ex.getMessage();
            Log.e("ERROR", "create url false:" + Error);
            throw new RuntimeException("The network is too bad!");
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
