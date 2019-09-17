package com.follow.nobahar.Manager;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String EnKey = "60c6c7ff35a5979b461b2136cd13b0ff";
    private static final String EnIV = "0000000000000000";

    public static String encryption(String stringToEncryption) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(EnKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(EnIV.getBytes());
            assert cipher != null;
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.encodeToString(cipher.doFinal(stringToEncryption.getBytes("UTF-8")), Base64.DEFAULT).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String decryption(String stringToDecryption) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(EnKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(EnIV.getBytes());
            assert cipher != null;
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return new String(cipher.doFinal(Base64.decode(stringToDecryption, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
