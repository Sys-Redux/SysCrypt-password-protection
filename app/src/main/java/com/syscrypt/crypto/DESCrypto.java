package com.syscrypt.crypto;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class DESCrypto implements CryptoEngine {

    private static final String ALGORITHM = "DES";
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    public String encrypt(String plainText, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String cipherText, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = md.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public int getRequiredKeyLength() {
        return 1;
    }

    public String getAlgorithmName() {
        return "DES";
    }
}