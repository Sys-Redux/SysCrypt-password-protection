package com.syscrypt.crypto;

public interface CryptoEngine {
    String encrypt(String plainText, String key) throws Exception;
    String decrypt(String cipherText, String key) throws Exception;
    int getRequiredKeyLength();
    String getAlgorithmName();
}