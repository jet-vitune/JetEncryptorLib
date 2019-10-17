package com.jetsynthesys.encryptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Pawan.jha on 10/4/2019.
 */

public class Encryption {

    private final static String TAG = "Encryption";

    private final Builder mBuilder;

    private Encryption(Builder builder) {
        mBuilder = builder;
    }

    public static Encryption getDefault(String key, String salt, byte[] iv) {
        try {
            return Builder.getDefaultBuilder(key, salt, iv).build();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String encrypt(String data) {
        try {
            if (data == null || data.isEmpty())
                return "";

            SecretKey secretKey = getSecretKey(hashTheKey(mBuilder.getKey()));
            byte[] dataBytes = data.getBytes(mBuilder.getCharsetName());
            Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
            String resdata = Base64.encodeToString(cipher.doFinal(dataBytes), mBuilder.getBase64Mode());

            return resdata;
        } catch (Exception e) {

        }

        return data;
    }

    public void encryptOrNull(String data, final Callback callback) {


        try {
            encryptAsync(data, callback);
        } catch (Exception e) {

        }
    }

    public void encryptAsync(final String data, final Callback callback) {
        if (callback == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String encrypt = encrypt(data);
                    if (encrypt == null) {
                        callback.onError(new Exception("Encrypt return null, it normally occurs when you send a null data"));
                    }
                    callback.onSuccess(encrypt);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        }).start();
    }

    public String decrypt(String data) {
        try {
            if (data == null || data.isEmpty())
                return "";

            byte[] dataBytes = Base64.decode(data, mBuilder.getBase64Mode());
            SecretKey secretKey = getSecretKey(hashTheKey(mBuilder.getKey()));
            Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
            byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
            return new String(dataBytesDecrypted);
        } catch (Exception e) {
            //Log.e("Encryption", e.toString());
        }

        return data;
    }

    public String decrypt(List<String> dataList, int position) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        if (dataList == null) return null;

        String decrypted = "";

        if (position < dataList.size()) {
            String data = dataList.get(position);
            try {
                byte[] dataBytes = Base64.decode(data, mBuilder.getBase64Mode());
                SecretKey secretKey = getSecretKey(hashTheKey(mBuilder.getKey()));
                Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
                cipher.init(Cipher.DECRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
                byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));

                decrypted = new String(dataBytesDecrypted);

            } catch (Exception e) {
                //Log.e("Encryption: ", e.toString());
            }
        }
        if (decrypted.isEmpty()) {
            return decrypt(dataList, position + 1);
        } else {
            return decrypted;
        }

    }


    private SecretKey getSecretKey(char[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(mBuilder.getSecretKeyType());
        KeySpec spec = new PBEKeySpec(key, mBuilder.getSalt().getBytes(mBuilder.getCharsetName()), mBuilder.getIterationCount(), mBuilder.getKeyLength());
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), mBuilder.getKeyAlgorithm());
    }


    private char[] hashTheKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mBuilder.getDigestAlgorithm());
        messageDigest.update(key.getBytes(mBuilder.getCharsetName()));
        return Base64.encodeToString(messageDigest.digest(), Base64.NO_PADDING).toCharArray();
    }

    public interface Callback {
        void onSuccess(String result);

        void onError(Exception exception);
    }

    private static class Builder {

        private byte[] mIv;
        private int mKeyLength;
        private int mBase64Mode;
        private int mIterationCount;
        private String mSalt;
        private String mKey;
        private String mAlgorithm;
        private String mKeyAlgorithm;
        private String mCharsetName;
        private String mSecretKeyType;
        private String mDigestAlgorithm;
        private String mSecureRandomAlgorithm;
        private SecureRandom mSecureRandom;
        private IvParameterSpec mIvParameterSpec;

        public static Builder getDefaultBuilder(String key, String salt, byte[] iv) {
            return new Builder()
                    .setIv(iv)
                    .setKey(key)
                    .setSalt(salt)
                    .setKeyLength(128)
                    .setKeyAlgorithm("AES")
                    .setCharsetName("UTF8")
                    .setIterationCount(1)
                    .setDigestAlgorithm("SHA1")
                    .setBase64Mode(Base64.DEFAULT)
                    .setAlgorithm("AES/CBC/PKCS5Padding")
                    .setSecureRandomAlgorithm("SHA1PRNG")
                    .setSecretKeyType("PBKDF2WithHmacSHA1");
        }

        private Encryption build() throws NoSuchAlgorithmException {
            setSecureRandom(SecureRandom.getInstance(getSecureRandomAlgorithm()));
            setIvParameterSpec(new IvParameterSpec(getIv()));
            return new Encryption(this);
        }

        private String getCharsetName() {
            return mCharsetName;
        }

        private Builder setCharsetName(String charsetName) {
            mCharsetName = charsetName;
            return this;
        }

        private String getAlgorithm() {
            return mAlgorithm;
        }

        private Builder setAlgorithm(String algorithm) {
            mAlgorithm = algorithm;
            return this;
        }

        private String getKeyAlgorithm() {
            return mKeyAlgorithm;
        }

        private Builder setKeyAlgorithm(String keyAlgorithm) {
            mKeyAlgorithm = keyAlgorithm;
            return this;
        }

        private int getBase64Mode() {
            return mBase64Mode;
        }

        private Builder setBase64Mode(int base64Mode) {
            mBase64Mode = base64Mode;
            return this;
        }

        private String getSecretKeyType() {
            return mSecretKeyType;
        }

        private Builder setSecretKeyType(String secretKeyType) {
            mSecretKeyType = secretKeyType;
            return this;
        }

        private String getSalt() {
            return mSalt;
        }

        private Builder setSalt(String salt) {
            mSalt = salt;
            return this;
        }

        private String getKey() {
            return mKey;
        }

        private Builder setKey(String key) {
            mKey = key;
            return this;
        }

        private int getKeyLength() {
            return mKeyLength;
        }

        public Builder setKeyLength(int keyLength) {
            mKeyLength = keyLength;
            return this;
        }

        private int getIterationCount() {
            return mIterationCount;
        }

        public Builder setIterationCount(int iterationCount) {
            mIterationCount = iterationCount;
            return this;
        }

        private String getSecureRandomAlgorithm() {
            return mSecureRandomAlgorithm;
        }

        public Builder setSecureRandomAlgorithm(String secureRandomAlgorithm) {
            mSecureRandomAlgorithm = secureRandomAlgorithm;
            return this;
        }

        private byte[] getIv() {
            return mIv;
        }

        public Builder setIv(byte[] iv) {
            mIv = iv;
            return this;
        }

        private SecureRandom getSecureRandom() {
            return mSecureRandom;
        }

        public Builder setSecureRandom(SecureRandom secureRandom) {
            mSecureRandom = secureRandom;
            return this;
        }

        private IvParameterSpec getIvParameterSpec() {
            return mIvParameterSpec;
        }

        public Builder setIvParameterSpec(IvParameterSpec ivParameterSpec) {
            mIvParameterSpec = ivParameterSpec;
            return this;
        }

        private String getDigestAlgorithm() {
            return mDigestAlgorithm;
        }

        public Builder setDigestAlgorithm(String digestAlgorithm) {
            mDigestAlgorithm = digestAlgorithm;
            return this;
        }

    }

    public static String getHashKey(Context pContext) {
        List<String> hashKeySet = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= 28) {

                @SuppressLint("WrongConstant") final PackageInfo packageInfo = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                final Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
                final MessageDigest md = MessageDigest.getInstance("SHA");
                for (Signature signature : signatures) {
                    md.update(signature.toByteArray());
                    final String signatureBase64 = new String(Base64.encode(md.digest(), Base64.DEFAULT));

                    //Log.e(TAG, "getHashKey HashKey >=28: " + signatureBase64);

                    return signatureBase64.replace("\n", "");
                }
            } else {
                PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String hashKey = new String(Base64.encode(md.digest(), 0));
                    hashKey = hashKey.replace("\n", "");
                    hashKeySet.add(hashKey);

                    //Log.e(TAG, "getHashKey HashKey >=28: " + hashKeySet.get(0));

                    return hashKeySet.get(0);
                }
            }


        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
        return "";
    }

}