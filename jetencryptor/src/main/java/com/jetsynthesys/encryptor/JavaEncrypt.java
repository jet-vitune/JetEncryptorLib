package com.jetsynthesys.encryptor;

import android.content.Context;
import android.util.Base64;


import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JavaEncrypt {

    /**
     * $cipherData = $data['param1'];
     * $RSAEncryptedKey = $data['param2'];
     * $hmacHash = $data['param3'];
     */




    public static String getData(Context mContext, String encryptData, String publicKeyString){
        try {

            /**
             * Perform AES on Encrypt Data
             */
            // 1. generate secret key using AES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // AES is currently available in three key sizes: 128, 192 and 256 bits.The design and strength of all key lengths of the AES algorithm are sufficient to protect classified information up to the SECRET level
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


/****************************************************************************************/
            // Generating IV.
            byte[] IV = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(IV);
           String  stringKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
           //String  stringIV = Base64.encodeToString(IV, Base64.DEFAULT);
            String stringIV = Hex.encodeHexString(IV);

            byte[] cipherText = encrypt(encryptData.getBytes(),secretKey, IV);
            String decryptedText = decrypt(cipherText,secretKey, IV);

            String cipheHex = Hex.encodeHexString(cipherText);

            String param1=stringIV+cipheHex;


/*******************************************************************************************/


            /**
             * Perform RSA on AES Secret Key
             */

            String publicKeyPEM = publicKeyString;
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
            publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
            byte[] encoded = Base64.decode(publicKeyPEM, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encoded));




            // 6. encrypt secret key using public key
            //Cipher cipher2 = Cipher.getInstance("RSA/NONE/OAEPPadding");
            //Cipher cipher2 = Cipher.getInstance("RSA/ECB/RSA_PKCS1_OAEP_PADDING");
            Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher2.init(Cipher.ENCRYPT_MODE, pubKey);
            String param2 = Base64.encodeToString(cipher2.doFinal(secretKey.getEncoded()), Base64.DEFAULT);


            JSONObject jsonObject =new JSONObject();
            jsonObject.put("param1",param1);
            jsonObject.put("param2",param2);
            jsonObject.put("param3",getHmacSha256Hash(param1));



//            DataModel dataModel = new DataModel();
//
//            dataModel.setParam1(param1);
//            dataModel.setParam2(param2);
//            dataModel.setParam3(param3);
//            String payLoad = new Gson().toJson(dataModel);



            return jsonObject.toString();
            //return payLoad;

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";


    }



    public static String getHmacSha256Hash(String param1) throws Exception {
        String key = "012345678";

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(param1.getBytes("UTF-8")));
    }


    public static String hexToBinary(String hex) {
        int i = Integer.parseInt(hex, 16);
        String bin = Integer.toBinaryString(i);
        return bin;
    }






    /**
     * AES Encrytption
     * @param plaintext
     * @param key
     * @param IV
     * @return
     * @throws Exception
     */
    public static byte[] encrypt (byte[] plaintext,SecretKey key,byte[] IV ) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        return cipherText;
    }

    /**
     * AES Decryption
     * @param cipherText
     * @param key
     * @param IV
     * @return
     * @throws Exception
     */
    public static String decrypt (byte[] cipherText, SecretKey key,byte[] IV) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);
    }














//    private String Encrypt(String encryptData, String publicKeyString) {
//
//        try {
//
//
//            /**
//             * Perform AES on Encrypt Data
//             */
//            // 1. generate secret key using AES
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//            keyGenerator.init(256); // AES is currently available in three key sizes: 128, 192 and 256 bits.The design and strength of all key lengths of the AES algorithm are sufficient to protect classified information up to the SECRET level
//            SecretKey secretKey = keyGenerator.generateKey();
//
//
//            // 3. encrypt string using secret key
//            byte[] raw = secretKey.getEncoded();
//            String secret = raw.toString();
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
//            String iv = ivParameterSpec.getIV().toString();
//
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
//            String cipherTextString = Base64.encodeToString(cipher.doFinal(encryptData.getBytes(Charset.forName("UTF-8"))), Base64.DEFAULT);
//
//
//            String param1 = iv + cipherTextString;
//
//            /**
//             * Perform RSA on AES Secret Key
//             */
//
//            // 4. get public key
//            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            PublicKey publicKey = keyFactory.generatePublic(publicSpec);
//
//            // 6. encrypt secret key using public key
//            Cipher cipher2 = Cipher.getInstance("RSA/ECB/RSA_PKCS1_OAEP_PADDING");
//            cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
//
//            String param2 = Base64.encodeToString(cipher2.doFinal(raw), Base64.DEFAULT);
//
//            String param3 = getHmacSha256Hash(param1);
//
//            DataModel dataModel = new DataModel();
//
//            dataModel.setParam1(param1);
//            dataModel.setParam2(param2);
//            dataModel.setParam3(param3);
//
//            String payLoad = new Gson().toJson(dataModel);
//
//            return payLoad;
//
//        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return "";
//    }


//    private static void Decrypt(String param1, String param2, String param3, String privKey) {
//        try {
//
//            /**
//             *Step- 1 Check Hash of Data
//             */
//
//            if (param3.equalsIgnoreCase(getHmacSha256Hash(param1))) {
//                //Hash Is Okay You can proceed
//            } else {
//                return;
//            }
//
//
//            /**
//             * Step- 2 Decrypt AES key using RSA Algo
//             */
//
//            // 1. Get private key
//            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.decode(privKey, Base64.DEFAULT));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            PrivateKey privateKey = keyFactory.generatePrivate(privateSpec);
//
//            // 2. Decrypt encrypted secret key using private key
//            Cipher cipher1 = Cipher.getInstance("RSA/ECB/RSA_PKCS1_OAEP_PADDING");
//            cipher1.init(Cipher.DECRYPT_MODE, privateKey);
//            byte[] secretKeyBytes = cipher1.doFinal(Base64.decode(param2, Base64.DEFAULT));
//            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
//
//            /**
//             * Step- 3 Decrypt encrypted text using secret key
//             */
//
//            String ivHex = param1.substring(0, Math.min(param1.length(), 32));
//            String iv = hexToBinary(ivHex);
//            String cipherData = param1.substring(33, param1.length());
//
//            byte[] raw = secretKey.getEncoded();
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv.getBytes()));
//            byte[] original = cipher.doFinal(Base64.decode(cipherData, Base64.DEFAULT));
//            String originalText = new String(original, Charset.forName("UTF-8"));
//            // 4. Print the original text sent by client
//            System.out.println("Decrypted \n" + originalText + "\n\n");
//
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    //            RSA_ECB_NO_PADDING ("RSA/ECB/NoPadding"),
//                    RSA_ECB_PKCS1PADDING ("RSA/ECB/PKCS1Padding"),
//                    RSA_ECB_OAEPPadding ("RSA/ECB/OAEPPadding"),
//                    RSA_ECB_PKCS1Padding ("RSA/ECB/PKCS1Padding"),
//                    RSA_None_NoPadding ("RSA/None/NoPadding"),
//
//                    RSA_ECB_OAEP_with_MD5_and_MGF1_PADDING("RSA/ECB/OAEPWithMD5AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA1_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA1AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA_1_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA_224_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA-224AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA_256_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA_384_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA-384AndMGF1Padding"),
//                    RSA_ECB_OAEP_with_SHA_512_and_MGF1_PADDING("RSA/ECB/OAEPWithSHA-512AndMGF1Padding ");




}
