package com.example.jzy.helloword;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.inbot.padbot.PadBotSdk;
import com.inbot.padbot.domain.RobotVo;
import com.inbot.padbot.listener.PadBotSDKListener;

public class ControlPadbotService extends Service implements PadBotSDKListener{
    public static final String TAG = ControlPadbotService.class.getSimpleName();
    public ControlPadbotService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PadBotSdk.init(this);
        PadBotSdk.setPadBotSdkListener(this);
        PadBotSdk.connectRobot();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "robot onConnected : " );
        if ((android.os.Build.MODEL.equals("PADBOT-R610"))) {

            //get robot serial number
            Log.i(TAG,"robot onConnected --> "+android.os.Build.MODEL);
            PadBotSdk.getRobotSerialNumber();
        }
    }

    @Override
    public void onConnectFailed(int messageCode) {
        Log.i(TAG, "Failed to connect : " + messageCode);
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "disconnect");
    }

    @Override
    public void onReturnRobotVersion(String version) {
        Log.i(TAG, "robot version : " + version);
    }

    @Override
    public void onReturnRobotSerialNumber(String serialNumber) {
        Log.i(TAG, "robot serial number : " + serialNumber);

        RobotVo robotVo = new RobotVo();
        robotVo.setSerialNumber(serialNumber);
        robotVo.setModelName("PadBot P1");

     /*   //start RobotControlActivity
        Intent intent = new Intent();
        intent.setClass(RobotScanActivity.this, RobotControlActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("connectDevice", robotVo);
        intent.putExtras(mBundle);
        startActivityForResult(intent, 0);*/
    }

    @Override
    public void onReturnRobotVoltage(double vlotage) {
        Log.i(TAG, "robot vlotage : " + vlotage);
    }

    @Override
    public void onReturnRobotBottomInfrared(double[] infrareds) {
        String infraredsStr = null;
        for (double infrared : infrareds) {
            if (null == infraredsStr) {
                infraredsStr = "" + infrared;
            }
            else {
                infraredsStr += "," + infrared;
            }
        }
        Log.i(TAG, "robot bottomInfrared : " + infraredsStr);
    }

    @Override
    public void onReturnRobotTopInfrared(double[] doubles) {

    }

    @Override
    public void onReturnRobotAutoCharge(int i) {

    }
}
