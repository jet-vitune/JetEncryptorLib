package com.jetsynthesys.jetencryptorjava;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.jetsynthesys.encryptor.BuildConfig;
import com.jetsynthesys.encryptor.JetEncryptor;
import com.jetsynthesys.encryptor.JobListener;

public class MainActivity extends AppCompatActivity implements JobListener{
    public static final String TAG = "MAINACTIVITY";
    JetEncryptor jetEncryptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        JetEncryptor.getInstance().initInBackgroundWithJetEngage(this, this, true,
                "cf-pp.jetsynthesys.com",443, "/api/v1/Auth/get",
                "in.publicam.jetengage.channelfight_pp","abcdseetrfgf234");


/**
 *
 *  params.put("Content-type", "application/json");
 *                 params.put("DEVICE-ID", "abcd243435");
 *                 params.put("APP-SIGNATURE", getPackageHash(mContext));
 *                 params.put("PACKAGE-NAME", "in.publicam.advancesalary");
 *
 *   String url = "http://114.143.181.228/poonawalla/v1/auth/token";
 *
 */




    }


    public void callApiFor() {

        // String myjson="{\"mobile_number\":\"7776003630\",\"pin_number\":\"123456\",\"locale\":{\"country\":\"IN\",\"platform\":\"android\",\"version\":\"2.0\",\"version_code\":8,\"language\":\"en\",\"segment\":\"\",\"device\":\"Xiaomi\",\"model\":\"jasmine_sprout\"}}";


        JSONObject requestObject = new JSONObject();
        JSONObject localJson = null;

        try {
            requestObject.put("mobile_number", "7776003630");
            requestObject.put("pin_number", "123456");
            requestObject.put("locale", getLocalJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String data = jetEncryptor.getInstance().processData(MainActivity.this, requestObject.toString());


        Log.e("EEEEEE", " Encrypted Data " + data);


    }


    public static JSONObject getLocalJson() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", "IN");
            jsonObject.put("platform", "android");
            jsonObject.put("version", com.jetsynthesys.encryptor.BuildConfig.VERSION_NAME);
            jsonObject.put("version_code", BuildConfig.VERSION_CODE);
            jsonObject.put("language", "en");
            jsonObject.put("segment", "");
            jsonObject.put("device", "" + Build.MANUFACTURER);
            jsonObject.put("model", "" + Build.DEVICE);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void workStarted(int workid) {

        Log.e(TAG, "workStarted: "+workid);
    }

    @Override
    public void workFinished(int workid) {

        Log.e(TAG, "workFinished: "+workid);
    }

    @Override
    public void workResult(String Result) {

        Log.e(TAG, "workResult: "+Result);
    }

    @Override
    public void onworkError(String error) {

        Log.e(TAG, "onworkError: "+error);
    }
}
