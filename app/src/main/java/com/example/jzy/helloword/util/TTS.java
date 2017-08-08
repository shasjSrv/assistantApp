package com.example.jzy.helloword.util;

import android.os.Environment;
import android.util.Log;

import com.example.jzy.helloword.ChatActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by jzy on 8/8/17.
 */

public class TTS {
    private SpeechSynthesizer mTts;
    private boolean initSuccess = true;
    public TTS(){
        mTts = SpeechSynthesizer.createSynthesizer(ChatActivity.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(ChatActivity.TAG, "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    ChatActivity.showTip("初始化失败,错误码：" + code);
                    initSuccess = false;
                }
            }
        });
    }


    public void startSpeaking(String answerText,SynthesizerListener mTtsListener){
        setParamTTS();
        mTts.startSpeaking(answerText, mTtsListener);
    }


    private void setParamTTS() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 设置合成
        mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");// 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    public void destory(){
        mTts.stopSpeaking();
        mTts.destroy();
    }
}
