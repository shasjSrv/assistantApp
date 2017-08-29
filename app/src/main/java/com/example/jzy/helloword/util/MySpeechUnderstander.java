package com.example.jzy.helloword.util;

import android.os.Environment;
import android.util.Log;

import com.example.jzy.helloword.ChatActivity;
import com.example.jzy.helloword.HomePageActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUnderstanderListener;

/**
 * Created by jzy on 8/8/17.
 */

public class MySpeechUnderstander {
    private com.iflytek.cloud.SpeechUnderstander mSpeechUnderstander;
    private int ret = 0;// 函数调用返回值

    public MySpeechUnderstander(){
        mSpeechUnderstander = com.iflytek.cloud.SpeechUnderstander.createUnderstander(HomePageActivity.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(HomePageActivity.TAG, "speechUnderstanderListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    HomePageActivity.showTip("初始化失败,错误码：" + code);
                }
            }
        });
    }


    public void startUnderStanding(SpeechUnderstanderListener speechUnderstandListener){
        setParamUnderstand();

        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            HomePageActivity.showTip("停止录音");
        } else {
            ret = mSpeechUnderstander.startUnderstanding(speechUnderstandListener);
            if (ret != 0) {
                HomePageActivity.showTip("语义理解失败,错误码:" + ret);

            } else {
                HomePageActivity.showTip("开始");
            }
        }
    }
    public void stopUnderStanding(SpeechUnderstanderListener speechUnderstandListener){
        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            HomePageActivity.showTip("停止录音");
        }
    }

    public void setParamUnderstand() {

        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "700");

        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/sud.wav");
    }

    public void destory(){
        mSpeechUnderstander.cancel();
        mSpeechUnderstander.destroy();
    }
}
