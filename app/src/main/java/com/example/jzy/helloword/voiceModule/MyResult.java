package com.example.jzy.helloword.voiceModule;

import org.bytedeco.javacpp.presets.opencv_core;

/**
 * Created by jzy on 8/26/17.
 */

public class MyResult {
    String result;
    String text;
    public MyResult(String result, String text){
        this.result = result;
        this.text = text;
    }

    public String getResult(){return result;}
    public String getText(){return text;}
    public void setText(String text){this.text = text;}
    public void setResult(String result){this.result = result;}
}
