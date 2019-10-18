package com.jetsynthesys.encryptor;


import android.content.Context;

public class JetEncryptor {

    private static final String TAG = "JetEncryptor";

    private static JetEncryptor ourInstance;

    public static JetEncryptor getInstance() {
        if (ourInstance == null) {
            ourInstance = new JetEncryptor();
        }
        EncryptorBase.getInstance();
        return ourInstance;
    }

    public int initInBackground(Context mContext,
                                final JobListener mJobListener,
                                boolean isSecure,
                                String hostname,
                                int port,
                                String endPoint,
                                String packgeName,
                                String deviceId) {

        EncryptorBase.getInstance().initLib(mContext,
                packgeName,
                deviceId,
                mJobListener,
                isSecure,
                hostname);

        return 0;
    }

    public int initInBackgroundWithJetEngage(Context mContext,
                                             final JobListener mJobListener,
                                             boolean isSecure,
                                             String hostname,
                                             int port,
                                             String endPoint,
                                             String packgeName,
                                             String deviceId) {

        EncryptorBase.getInstance().initLibForJetEngage(mContext,
                packgeName,
                deviceId,
                mJobListener,
                isSecure,
                hostname);

        return 0;
    }

    public void cancelInitEcryptor() {

        if (EncryptorBase.getInstance().responseBodyCall != null && !EncryptorBase.getInstance().responseBodyCall.isCanceled()) {
            EncryptorBase.getInstance().responseBodyCall.cancel();
            EncryptorBase.getInstance().responseBodyCall = null;
        }
    }

    public String processData(Context mContext, String inputdata) {
        if (getRsaKey() != null || !getRsaKey().isEmpty()) {

            return EncryptorBase.getInstance().processData(mContext, inputdata);
        } else {
            //Log.e(TAG, "RSA Key is Null or Empty");
            return "";
        }
    }

    public boolean isInilized() {

        return EncryptorBase.getInstance().isInilized;
    }

    protected String getRSAPubKey() {
        if (EncryptorBase.getInstance().rsaKey != null && !EncryptorBase.getInstance().rsaKey.isEmpty()) {
            return EncryptorBase.getInstance().rsaKey;
        } else {
            if (EncryptorBase.getInstance().sharedPreferences != null) {
                try {
                    String decryptedRsaKey = EncryptorBase.getInstance().encryption.decrypt(EncryptorBase.getInstance().sharedPreferences.getString(EncryptorBase.RSA, ""));
                    return decryptedRsaKey;
                } catch (Exception e) {
                    //Log.e(TAG, "getRSAPubKey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    protected String getRsaKey() {

        if (EncryptorBase.getInstance().rsaKey != null && !EncryptorBase.getInstance().rsaKey.isEmpty()) {
            return EncryptorBase.getInstance().rsaKey;
        } else {
            if (EncryptorBase.getInstance().sharedPreferences != null) {
                try {
                    String decryptedRsaKey = EncryptorBase.getInstance().encryption.decrypt(EncryptorBase.getInstance().sharedPreferences.getString(EncryptorBase.RSA, ""));
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

    public String getJwtkey() {
        if (EncryptorBase.getInstance().jwtkey != null && !EncryptorBase.getInstance().jwtkey.isEmpty()) {
            return EncryptorBase.getInstance().jwtkey;
        } else {
            if (EncryptorBase.getInstance().sharedPreferences != null) {
                try {
                    String decryptedJwtKey = EncryptorBase.getInstance().encryption.decrypt(EncryptorBase.getInstance().sharedPreferences.getString(EncryptorBase.JWT, ""));
                    return decryptedJwtKey;
                } catch (Exception e) {
                    //Log.e(TAG, "getJwtkey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    public String getCertKey() {
        if (EncryptorBase.getInstance().cert != null && !EncryptorBase.getInstance().cert.isEmpty()) {
            return EncryptorBase.getInstance().cert;
        } else {
            if (EncryptorBase.getInstance().sharedPreferences != null) {
                try {
                    String decryptedCert = EncryptorBase.getInstance().encryption.decrypt(EncryptorBase.getInstance().sharedPreferences.getString(EncryptorBase.CERT, ""));
                    return decryptedCert;
                } catch (Exception e) {
                    //Log.e(TAG, "getCertKey: ", e);
                    return "";
                }
            } else {
                return "";
            }
        }
    }

}
