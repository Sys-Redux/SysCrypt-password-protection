package com.syscrypt.crypto;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class DESCrypto implements CryptoEngine {

    private static final String ALGORITHM = "DES";
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 8;

    public String encrypt(String plainText, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);

        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Prepend IV to encrypted data
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    public String decrypt(String cipherText, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);

        // Decode and extract IV
        byte[] combined = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(combined, 0, IV_SIZE);
        byte[] encryptedBytes = Arrays.copyOfRange(combined, IV_SIZE, combined.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

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