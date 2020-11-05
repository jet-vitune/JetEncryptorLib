package com.jetsynthesys.encryptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import com.google.gson.Gson;
import com.jetsynthesys.callback.ApiManager;

import retrofit2.Call;
import retrofit2.Callback;

public class EncryptorBase {


    public static final String RSA = "sra";
    public static final String JWT = "wjt";
    public static final String CERT = "ecrt";

    protected String jwtkey;
    protected String rsaKey;
    protected String cert;
    protected boolean isInilized = false;

    protected Call<ResponseBody> responseBodyCall;
    protected Call<WakauResponseBody> responseBodyCallWakau;
    protected Encryption encryption;
    protected SharedPreferences sharedPreferences;
    private static EncryptorBase jetEncryptorBase;

    public EncryptorBase() {

    }

    public static EncryptorBase getInstance() {

        if (jetEncryptorBase == null) {
            jetEncryptorBase = new EncryptorBase();
        }
        return jetEncryptorBase;
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

    public void initLib(final Context mContext,
                        final String packageName,
                        final String deviceId,
                        final JobListener mJobListener,
                        final boolean isSecure,
                        final String hostname) {

        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {

        }

        if (isSecure) {
            url = "https://" + hostname + "/";
        } else {
            url = "http://" + hostname + "/";
        }

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
                    //Log.e(TAG, "onFailure: ", e);
                }
            }
        });

    }

    public void initLibForWakau(final Context mContext,
                                final String packageName,
                                final String deviceId,
                                final JobListener mJobListener,
                                final boolean isSecure,
                                final String hostname,
                                final String shaKey) {

        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {

        }

        if (isSecure) {
            url = "https://" + hostname + "/";
        } else {
            url = "http://" + hostname + "/";
        }

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

        responseBodyCallWakau = ApiManager.getApiInstance(url, mContext).
                getEncryptorDataWakau("application/json",
                        Java_AES_Cipher.encrypt(shaKey, Java_AES_Cipher.getIV(),getPackageHash(mContext)),
                        Java_AES_Cipher.encrypt(shaKey, Java_AES_Cipher.getIV(),deviceId),
                        Java_AES_Cipher.encrypt(shaKey, Java_AES_Cipher.getIV(),packageName),
                        requestModel);

        responseBodyCallWakau.enqueue(new Callback<WakauResponseBody>() {
            @Override
            public void onResponse(Call<WakauResponseBody> call, retrofit2.Response<WakauResponseBody> response) {
                try {

                    WakauResponseBody encryptedResponse = response.body();
                    String decryptedResponse = Java_AES_Cipher.decrypt(shaKey, encryptedResponse.getData());
                    ResponseBody responseBody =  new Gson().fromJson(decryptedResponse, ResponseBody.class);

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
            public void onFailure(Call<WakauResponseBody> call, Throwable t) {
                try {
                    mJobListener.onworkError(t.toString());
                } catch (Exception e) {
                    //Log.e(TAG, "onFailure: ", e);
                }
            }
        });

    }

    public void initLibForJetEngage(final Context mContext,
                                    final String packageName,
                                    final String deviceId,
                                    final JobListener mJobListener,
                                    final boolean isSecure,
                                    final String hostname) {

        try {
            if (sharedPreferences == null)
                sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

            if (encryption == null) {
                encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
            }
        } catch (Exception e) {
            //Log.e(TAG, "initialize: ", e);
        }

        if (isSecure) {
            url = "https://" + hostname + "/";
        } else {
            url = "http://" + hostname + "/";
        }

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
                    //Log.e(TAG, "onFailure: ", e);
                }
            }
        });

    }

    public void cancelInitEcryptor() {

        if (responseBodyCall != null && !responseBodyCall.isCanceled()) {

            responseBodyCall.cancel();
            responseBodyCall = null;
        }

        if (responseBodyCallWakau != null && !responseBodyCallWakau.isCanceled()) {

            responseBodyCallWakau.cancel();
            responseBodyCallWakau = null;
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


    public String processData(Context mContext, String inputdata) {
        if (getRsaKey() != null || !getRsaKey().isEmpty()) {

            try {
                if (sharedPreferences == null)
                    sharedPreferences = mContext.getSharedPreferences("aPMqAPaTRVAJA", Context.MODE_PRIVATE);

                if (encryption == null) {
                    encryption = Encryption.getDefault(Encryption.getHashKey(mContext), "Salt", new byte[16]);
                }
            } catch (Exception e) {
                //Log.e(TAG, "initialize: ", e);
            }

            return JavaEncrypt.getData(mContext, inputdata, getRsaKey());
        } else {
            //Log.e(TAG, "RSA Key is Null or Empty");
            return "";
        }
    }

    private void setInilized(boolean inilized) {

        isInilized = inilized;
    }

    protected void setRsaKey(String rsaKey) {

        this.rsaKey = rsaKey;

        try {
            String encryptedRsaKey = encryption.encrypt(rsaKey);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(EncryptorBase.RSA, encryptedRsaKey).apply();
        } catch (Exception e) {
        }
    }

    public void setJwtkey(String jwtkey) {

        this.jwtkey = jwtkey;

        try {
            String enCryptedjwtkey = encryption.encrypt(jwtkey);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(EncryptorBase.JWT, enCryptedjwtkey).apply();
        } catch (Exception e) {
            //Log.e(TAG, "setJwtkey: ", e);
        }
    }

    public void setCert(String cert) {

        this.cert = cert;

        try {
            String encryptedCert = encryption.encrypt(cert);
            if (sharedPreferences != null)
                sharedPreferences.edit().putString(EncryptorBase.CERT, encryptedCert).apply();
        } catch (Exception e) {
            //Log.e(TAG, "setCert: ", e);
        }
    }

    protected String getRsaKey() {

        if (rsaKey != null && !rsaKey.isEmpty()) {
            return rsaKey;
        } else {
            if (sharedPreferences != null) {
                try {
                    String decryptedRsaKey = encryption.decrypt(sharedPreferences.getString(EncryptorBase.RSA, ""));
                    return decryptedRsaKey;
                } catch (Exception e) {
                    //Log.e(TAG, "getrsa: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

}
