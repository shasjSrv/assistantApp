package com.example.jzy.helloword.voiceModule;

/**
 * Created by jzy on 8/26/17.
 */

public class MyResult {
    String result;
    String text;
    MyResult(String result,String text){
        this.result = result;
        this.text = text;
    }
    public String getResult(){return result;}
    public String getText(){return text;}
}
