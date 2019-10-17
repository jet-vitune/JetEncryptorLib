package com.jetsynthesys.encryptor;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jetsynthesys.callback.ApiManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class JetEncryptor {

    private static final String TAG = "JetEncryptor";

    private static final String RSA = "sra";
    private static final String JWT = "wjt";
    private static final String CERT = "ecrt";
    
    protected Gson gson;
    private ResponseModel responseModel;
    private String jwtkey;
    private String rsaKey;
    private String cert;
    private boolean isInilized = false;
    private Call<ResponseBody> responseBodyCall;

    private static Encryption encryption;
    private static SharedPreferences sharedPreferences;

    private static JetEncryptor ourInstance;//= new JetEncryptor();

    /**
     * getInstance of JetEncryptor
     *
     * @return JetEncryptor instance
     */
    public static JetEncryptor getInstance() {
        if (ourInstance == null) {
            ourInstance = new JetEncryptor();
        }
        return ourInstance;
    }


    /**
     * Init SDK and Params
     *
     * @param mContext     Context of Caller
     * @param mJobListener JobListener of Caller
     * @param isSecure     is URL for Socket or SecureSocket
     * @param hostname     ip address or hostname
     * @param port         port on which to listen
     * @param endPoint     endpoint of API
     * @param packgeName   package name of App
     * @param deviceId     Unique Device ID
     * @return 1 (Success) / -1 (Failure)
     */
    public int initialize(Context mContext, final JobListener mJobListener, boolean isSecure, String hostname,
                          int port, String endPoint, String packgeName, String deviceId) {


        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {
            Log.e(TAG, "initialize: ", e);
        }


        if (isSecure) {
            this.url = "https://" + hostname + "/";
        } else {
            this.url = "http://" + hostname + "/";
        }

        initLib(mContext, url, packgeName, deviceId, mJobListener);

        return 0;
    }

    public int initializeWithJetEngage(Context mContext, final JobListener mJobListener,
                                       boolean isSecure, String hostname, String packgeName, String deviceId) {

        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {
            Log.e(TAG, "initialize: ", e);
        }

        if (isSecure) {
            this.url = "https://" + hostname + "/";
        } else {
            this.url = "http://" + hostname + "/";
        }

        initLibForJetEngage(mContext, url, packgeName, deviceId, mJobListener);

        return 0;
    }


    /**
     * init SDK and Params in Background
     * This method will use AsyncTask to get all Params from Server
     *
     * @param mContext Context of Caller
     * @param mJobListener JobListener of Caller
     * @param isSecure is URL for Socket or SecureSocket
     * @param hostname ip address or hostname
     * @param port      port on which to listen
     * @param endPoint  endpoint of API
     * @param packgeName  package name of App
     * @param deviceId  Unique Device ID
     * @return 1 (Success) / -1 (Failure)
     */

    String url;

    public int initInBackground(Context mContext, final JobListener mJobListener, boolean isSecure, String hostname,
                                int port, String endPoint, String packgeName, String deviceId) {


        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {
            Log.e(TAG, "initialize: ", e);
        }

        if (isSecure) {
            url = "https://" + hostname + "/";
        } else {
            url = "http://" + hostname + "/";
        }
        initLib(mContext, url, packgeName, deviceId, mJobListener);
        return 0;
    }

    public int initInBackgroundWithJetEngage(Context mContext, final JobListener mJobListener, boolean isSecure, String hostname,
                                             int port, String endPoint, String packgeName, String deviceId) {


        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {
            Log.e(TAG, "initialize: ", e);
        }

        if (isSecure) {
            url = "https://" + hostname + "/";
        } else {
            url = "http://" + hostname + "/";
        }
        initLibForJetEngage(mContext, url, packgeName, deviceId, mJobListener);
        return 0;
    }


    public void initLib(final Context mContext, final String url, final String packageName, final String deviceId, final JobListener mJobListener) {

        mJobListener.workStarted(100);

        RequestModel requestModel = new RequestModel();
        RequestModel.LocaleBean localeBean = new RequestModel.LocaleBean();


        localeBean.setCountry("IN");
        localeBean.setPlatform("android");
        localeBean.setVersion(BuildConfig.VERSION_NAME);
        localeBean.setVersion_code(BuildConfig.VERSION_CODE);
        localeBean.setLanguage("en");
        localeBean.setSegment("");
        localeBean.setDevice("" + Build.MANUFACTURER);
        localeBean.setModel("" + Build.DEVICE);

        requestModel.setLocale(localeBean);

        responseBodyCall = ApiManager.getApiInstance(url, mContext).
                getEncryptorData("application/json",
                        getPackageHash(mContext),
                        deviceId,
                        packageName,
                        requestModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {

                    ResponseBody responseBody = response.body();

                    if (responseBody.getCode() == 200) {

                        if (responseBody.getData().getKey() != null) {
                            setRsaKey(responseBody.getData().getKey());
                        }

                        if (responseBody.getData().getJwt() != null) {

                            setJwtkey(responseBody.getData().getJwt());
                        }

                        if (responseBody.getData().getCert() != null) {
                            setCert(responseBody.getData().getCert());
                        }


                        setInilized(true);
                        mJobListener.workFinished(100);
                        try {
                            mJobListener.workResult(responseBody.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        //TODO
                        mJobListener.onworkError("Message: " + responseBody.getMessage() + " Code: " + responseBody.getCode());
                    }

                } catch (Exception e) {
                    mJobListener.onworkError(e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    mJobListener.onworkError(t.toString());
                } catch (Exception e) {
                    Log.e(TAG, "onFailure: ", e);
                }
            }
        });

    }

    public void initLibForJetEngage(final Context mContext, final String url, final String packageName, final String deviceId, final JobListener mJobListener) {

        mJobListener.workStarted(100);

        RequestModel requestModel = new RequestModel();
        RequestModel.LocaleBean localeBean = new RequestModel.LocaleBean();


        localeBean.setCountry("IN");
        localeBean.setPlatform("android");
        localeBean.setVersion(BuildConfig.VERSION_NAME);
        localeBean.setVersion_code(BuildConfig.VERSION_CODE);
        localeBean.setLanguage("en");
        localeBean.setSegment("");
        localeBean.setDevice("" + Build.MANUFACTURER);
        localeBean.setModel("" + Build.DEVICE);

        requestModel.setLocale(localeBean);

        responseBodyCall = ApiManager.getApiInstance(url, mContext).
                getEncryptorDataJetEngage("application/json",
                        getPackageHash(mContext),
                        deviceId,
                        packageName,
                        requestModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {

                    ResponseBody responseBody = response.body();

                    if (responseBody.getCode() == 200) {

                        if (responseBody.getData().getKey() != null) {
                            setRsaKey(responseBody.getData().getKey());
                        }

                        if (responseBody.getData().getJwt() != null) {
                            setJwtkey(responseBody.getData().getJwt());
                        }

                        if (responseBody.getData().getCert() != null) {
                            setCert(responseBody.getData().getCert());
                        }


                        setInilized(true);
                        mJobListener.workFinished(100);
                        try {
                            mJobListener.workResult(responseBody.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        //TODO
                        mJobListener.onworkError("Message: " + responseBody.getMessage() + " Code: " + responseBody.getCode());
                    }

                } catch (Exception e) {
                    mJobListener.onworkError(e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    mJobListener.onworkError(t.toString());
                } catch (Exception e) {
                    Log.e(TAG, "onFailure: ", e);
                }
            }
        });

    }

    public void cancelInitEcryptor() {

        if (responseBodyCall != null && !responseBodyCall.isCanceled()) {

            responseBodyCall.cancel();
            responseBodyCall = null;
        }
    }

    /**
     * getPackageHash(Context mContext)
     * Method to generate log of App Signature
     *
     * @param mContext
     * @return
     */
    public String getPackageHash(Context mContext) {
        String sign = "";
        try {
            Signature[] sigs = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            for (Signature sig : sigs) {
//                printlog("MyApp", "Signature hashcode : " + sig.hashCode());
//                printlog("MyApp", "Signature String : " + sig.toCharsString());
                sign = sig.toCharsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    private static void printlog(String tag, String msg) {
        //Log.e(tag,msg);
    }


    public static JSONObject getLocalJson() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("country", "IN");
            jsonObject.put("platform", "android");
            jsonObject.put("version", BuildConfig.VERSION_NAME);
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

    public String processData(Context mContext, String inputdata) {
        if (getRsaKey() != null || !getRsaKey().isEmpty()) {

            try {
                if (sharedPreferences == null)
                    sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

                if (encryption == null) {
                    encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
                }
            } catch (Exception e) {
                Log.e(TAG, "initialize: ", e);
            }

            return JavaEncrypt.getData(mContext, inputdata, getRsaKey());
        } else {
            Log.e(TAG, "RSA Key is Null or Empty");
            return "";
        }
    }


    public boolean isInilized() {

        return isInilized;
    }


    private void setInilized(boolean inilized) {

        isInilized = inilized;
    }

    protected String getRSAPubKey() {
        if (rsaKey != null && !rsaKey.isEmpty()) {
            return rsaKey;
        } else {
            if (sharedPreferences != null) {
                try {
                    String decryptedRsaKey = encryption.decrypt(sharedPreferences.getString(JetEncryptor.RSA, ""));
                    return decryptedRsaKey;
                } catch (Exception e) {
                    Log.e(TAG, "getRSAPubKey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    protected String getRsaKey() {

        if (rsaKey != null && !rsaKey.isEmpty()) {
            return rsaKey;
        } else {
            if (sharedPreferences != null) {
                try {
                    String decryptedRsaKey = encryption.decrypt(sharedPreferences.getString(JetEncryptor.RSA, ""));
                    return decryptedRsaKey;
                } catch (Exception e) {
                    Log.e(TAG, "getrsa: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    protected void setRsaKey(String rsaKey) {

        this.rsaKey = rsaKey;

        try {
            String encryptedRsaKey = encryption.encrypt(rsaKey);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(JetEncryptor.RSA, encryptedRsaKey).apply();
        } catch (Exception e) {
        }
    }


    public String getJwtkey() {
        if (jwtkey != null && !jwtkey.isEmpty()) {
            return jwtkey;
        } else {
            if (sharedPreferences != null) {
                try {
                    String decryptedJwtKey = encryption.decrypt(sharedPreferences.getString(JetEncryptor.JWT, ""));
                    return decryptedJwtKey;
                } catch (Exception e) {
                    Log.e(TAG, "getJwtkey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    public void setJwtkey(String jwtkey) {

        this.jwtkey = jwtkey;

        try {
            String enCryptedjwtkey = encryption.encrypt(jwtkey);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(JetEncryptor.JWT, enCryptedjwtkey).apply();
        } catch (Exception e) {
            Log.e(TAG, "setJwtkey: ", e);
        }
    }


    public String getCertKey() {
        if (cert != null && !cert.isEmpty()) {
            return cert;
        } else {
            if (sharedPreferences != null) {
                try {
                    String decryptedCert = encryption.decrypt(sharedPreferences.getString(JetEncryptor.CERT, ""));
                    return decryptedCert;
                } catch (Exception e) {
                    Log.e(TAG, "getCertKey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }


    public void setCert(String cert) {

        this.cert = cert;

        try {
            String encryptedCert = encryption.encrypt(cert);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(JetEncryptor.CERT, encryptedCert).apply();
        } catch (Exception e) {
            Log.e(TAG, "setCert: ", e);
        }
    }


}
