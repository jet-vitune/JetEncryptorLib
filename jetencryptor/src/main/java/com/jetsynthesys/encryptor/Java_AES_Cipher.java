package com.jetsynthesys.encryptor;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Java_AES_Cipher {

    private static final String TAG = "Java_AES_Cipher";

    private static String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private static int CIPHER_KEY_LEN = 16; //128 bits

    /**
     * Encrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param iv   - initialization vector
     * @param data - data to encrypt
     * @return encryptedData data in base64 encoding with iv attached at end after a :
     */

    public static String encrypt(String key, byte[] iv, String data) {
        try {
            if (key.length() < Java_AES_Cipher.CIPHER_KEY_LEN) {
                int numPad = Java_AES_Cipher.CIPHER_KEY_LEN - key.length();

                for (int i = 0; i < numPad; i++) {
                    key += "0"; //0 pad to len 16 bytes
                }

            } else if (key.length() > Java_AES_Cipher.CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }


            IvParameterSpec initVector = new IvParameterSpec(iv);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(Java_AES_Cipher.CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initVector);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));

            String base64_EncryptedData = encodeBase64(encryptedData);
            String base64_IV = encodeBase64(iv);

            String encryptedString = base64_EncryptedData + ":" + base64_IV;
            return encryptedString.replaceAll(System.getProperty("line.separator"), "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Decrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param data - encrypted data with iv at the end separate by :
     * @return decrypted data string
     */


    public static String decrypt(String key, String data) {
        try {

            if (key.length() < Java_AES_Cipher.CIPHER_KEY_LEN) {
                int numPad = Java_AES_Cipher.CIPHER_KEY_LEN - key.length();

                for (int i = 0; i < numPad; i++) {
                    key += "0"; //0 pad to len 16 bytes
                }

            } else if (key.length() > Java_AES_Cipher.CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }

            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(decodeBase64(parts[1]));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(Java_AES_Cipher.CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decodedEncryptedData = decodeBase64(parts[0]);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {

        }

        return null;
    }


    public static byte[] getIV() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] iv = random.generateSeed(16);
            String base64_IV = encodeBase64(iv);
            return iv;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static byte[] decodeBase64(String base64) {
        byte[] data = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        return data;
    }

    public static String encodeBase64(byte[] data) {
        String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        return base64;
    }

}

