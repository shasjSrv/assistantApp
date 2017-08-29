package com.example.jzy.helloword.util;

import android.util.Log;

import com.example.jzy.helloword.ChatActivity;
import com.example.jzy.helloword.HomePageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jzy on 8/8/17.
 */

public class HandleResult {
    //服务类型
    public static final int WEATHER = 1;
    public static final int OPENQA = 2;
    public static final int FQA = 3;
    public static final int BAIKE = 4;
    public static final int CHAT = 5;
    public static final int DATETIME = 6;
    public static final int OTHER = 7;
    public static final int MUSIC = 8;
    public static final int ANSWER = 9;
//    public static MyResult myResult;


    public static int whatService(String response) {
        try {
            JSONObject pSub = new JSONObject(response);
            String operation = pSub.getString("operation");
            String service = pSub.getString("service");
            if (operation.equals("QUERY")) {
                if (service.equals("weather")) {
                    return WEATHER;
                } else {
                    return OTHER;
                }
            } else if (operation.equals("ANSWER")) {
                return ANSWER;
            } else if (operation.equals("PLAY")) {
                if (service.equals("music")) {
                    return MUSIC;
                } else {
                    return OTHER;
                }
            } else {
                return OTHER;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
    }

    public static String parseWeather(String pMsg, String result, String text) {
        if (pMsg == null) {
            return null;
        }
        try {
            JSONObject weather = new JSONObject(pMsg);
            StringBuffer sb = new StringBuffer();
            if (weather == null) {
                Log.e(HomePageActivity.TAG, "WEATHER IS NULL");
                return null;
            } else {
                JSONObject semantic = weather.getJSONObject("semantic");
                if (semantic == null) {
                    Log.e(HomePageActivity.TAG, "semantic is null");
                    return null;
                } else {
                    String date;
                    JSONObject slots = semantic.getJSONObject("slots");
                    if (slots == null) {
                        Log.e(HomePageActivity.TAG, "slots is null");
                        return null;
                    } else {
                        JSONObject datetime = slots.getJSONObject("datetime");// 获得询问的日期
                        if (datetime == null) {
                            Log.e(HomePageActivity.TAG, "datetime is null");
                            return null;
                        } else {
                            date = datetime.getString("date");
                            sb.append(datetime.getString("dateOrig"));// 获得询问的时间（今天，明天，后天。。）
                        }
                        JSONObject location = slots.getJSONObject("location");
                        if (location == null) {
                            Log.e(HomePageActivity.TAG, "slots is null");
                            return null;
                        } else {
                            String addr = location.getString("city");
                            // 判断是不是问的“今天天气怎样？”如果是，重新询问
                            // TODO 日后加入GPS，直接定位到当前城市重新发送查询
                            if (addr.equals("CURRENT_CITY")) {
                                text = weather.getString("text");
                                /**
                                 * @TODO PLAY ANIMATION
                                 */
                                text = weather.getString("text");
                                return "咱们中国地方可大可大了，您问的是哪个位置的天气呀？";
                            }
                            sb.append(location.getString("cityAddr"));// 询问的城市
                            sb.append("的天气");
                            JSONObject data = weather.getJSONObject("data");
                            JSONArray resultArray = data.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultItem = resultArray.getJSONObject(i);
                                String itemDate = resultItem.getString("date");
                                if (itemDate.equals(date)) {
                                    sb.append(resultItem.getString("weather"));
                                    sb.append("，");
                                    sb.append(resultItem.getString("tempRange"));
                                    sb.append("，");
                                    sb.append(resultItem.getString("wind"));
                                    sb.append("。");
                                    break;
                                }
                            }
                            result = sb.toString();//机器人的回答
                            return result;//机器人的回答
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static String parseMusic(String pMsg){
        if(pMsg==null){
            return null;
        }

        try {
            JSONObject musicJson=new JSONObject(pMsg);
            JSONObject semantic=musicJson.getJSONObject("semantic");
            if(semantic==null){

                JSONObject data=musicJson.getJSONObject("data");
                JSONArray result=data.getJSONArray("result");
                JSONObject resultJson=result.getJSONObject(0);
                return resultJson.getString("downloadUrl");
            }
            //JSONObject slots=semantic.getJSONObject("slots");
            JSONObject slots=semantic.optJSONObject("slots");
            if(slots==null){
                Log.e(HomePageActivity.TAG, "22");
                JSONObject data=musicJson.getJSONObject("data");
                JSONArray result=data.getJSONArray("result");
                JSONObject resultJson=result.getJSONObject(0);
                return resultJson.getString("downloadUrl");
            }else if(slots.getString("artist")==null&&slots.getString("song")!=null){
                return null;
            }else{

                JSONObject data=musicJson.getJSONObject("data");
                JSONArray result=data.getJSONArray("result");
                JSONObject resultJson=result.getJSONObject(0);
                Log.e(HomePageActivity.TAG,resultJson.getString("downloadUrl"));
                return resultJson.getString("downloadUrl");
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }

    }

    public static MyResult parseAnswer(String pMsg, String result, String  text/*,Client cli*/) {
        if (pMsg == null) {
            return null;
        }
        try {
            JSONObject answerJson = new JSONObject(pMsg);
            /*JSONObject answerItem = answerJson.getJSONObject("answer");
            result = answerItem.getString("text");
            text = answerJson.getString("text");*/
            /*if(result != null && text != null) {
                MyResult myResult = new MyResult(result, text);
                return myResult;
            }else{*/
//                JSONObject resulatItem = answerJson.getJSONObject("result");
                text =  answerJson.getString("text");
                MyResult myResult = new MyResult(result, text);
                return myResult;
//            }
            // 机器人的回答
            ////////////////////////////
			/*if (text.equals("向前走。")) {
				sendData("forward\n");
				//sendData("1 0\n");
			} else if (text.equals("后退。")) {
				sendData("backward\n");
				//sendData("-1 0\n");
			} else if (text.equals("向左转。")) {
				sendData("turn left\n");
			} else if (text.equals("向右转。")) {
				sendData("turn right\n");
			} else if(text.equals("停下。")){
				// TODO 发送暂停。这里要结合时间来设置
				sendData("stop\n");
			}else if(text.equals("暂停。")){
				sendData("pause speech\n");
			}else if(text.equals("加速。")){
				sendData("faster\n");
			}else if(text.equals("减速。")){
				sendData("slower\n");
			}else if(text.equals("继续。")){
				sendData("continue speech\n");
			}else if(text.equals("左直角转弯。")){
				sendData("rotate left\n");
			}else if(text.equals("右直角转弯。")){
				sendData("rotate right\n");
			}else if(text.equals("全速。")){
				sendData("full speed\n");
			}else if(text.equals("一半最大速度。")){
				sendData("half speed\n");
			}else if(text.equals("四分之一速度。")){
				sendData("quarter speed\n");
			}



            /////////仿人///////////
            if (text.equals("向前走。")) {
                cli.sendCmd("Move_Forward\n");
                //sendData("1 0\n");
            } else if (text.equals("向后走。")) {
                cli.sendCmd("Move_Back\n");
                //sendData("-1 0\n");
            } else if (text.equals("左转。")) {
                cli.sendCmd("Turn_L\n");
                //sendData("-1 0\n");
            } else if (text.equals("右转。")) {
                cli.sendCmd("Turn_R\n");
                //sendData("-1 0\n");
            } else if (text.equals("挥手。")) {
                cli.sendCmd("Wave_L\n");
                //sendData("-1 0\n");
            } else if (text.equals("跳舞。")) {
                cli.sendCmd("Robot_Dance\n");
                //sendData("-1 0\n");
            } else if (text.equals("站好。")) {
                cli.sendCmd("Stand\n");
                //sendData("-1 0\n");
            } else if (text.equals("踢球。")) {
                cli.sendCmd("Kickball_L\n");
                //sendData("-1 0\n");
            } else if (text.equals("爬起来。")) {
                cli.sendCmd("Climb_Up\n");
                //sendData("-1 0\n");
            }*/
            //////////////////////


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

