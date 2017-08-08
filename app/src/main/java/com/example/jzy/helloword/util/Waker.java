package com.example.jzy.helloword.util;

import android.content.Context;
import android.util.Log;

import com.example.jzy.helloword.ChatActivity;
import com.example.jzy.helloword.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * Created by jzy on 8/8/17.
 */

public class Waker {
    private  int curThresh = 10;
    private  String keep_alive = "1";
    private  String ivwNetMode = "0";
    private VoiceWakeuper mIvw;
    private Context context;


    public Waker(){
        context = ChatActivity.getContext();
        mIvw = VoiceWakeuper.getWakeuper();
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    public boolean isNull(){
        return mIvw == null;
    }


    public void startListening(WakeuperListener mWakeuperListener){
        if (mIvw != null) {
            ChatActivity.showTip("唤醒开始");
            setParamWake();
            // 启动唤醒
            mIvw.startListening(mWakeuperListener);
        } else {
            ChatActivity.showTip("唤醒未初始化");
        }
    }
    public void setParamWake() {
        Log.i(ChatActivity.TAG, "start paramwake");
        // 清空参数.
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
    }

    public void destory(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
    }

    public  String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(context,ResourceUtil.RESOURCE_TYPE.assets, "ivw/"+context.getString(R.string.app_id)+".jet");
        Log.d(ChatActivity.TAG, "resPath: "+resPath );
        return resPath;
    }
}
