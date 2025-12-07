package com.syscrypt.crypto;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

public class RSACrypto implements CryptoEngine {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    // Cache key pairs to avoid regen
    private static final Map<String, KeyPair> keyCache = new HashMap<>();

    public String encrypt(String plainText, String key) throws Exception {
        KeyPair keyPair = getOrGenerateKeyPair(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String cipherText, String key) throws Exception {
        KeyPair keyPair = getOrGenerateKeyPair(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decodedBytes = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private KeyPair getOrGenerateKeyPair(String passphrase) throws Exception {
        if (keyCache.containsKey(passphrase)) {
            return keyCache.get(passphrase);
        }

        // Create deterministic random from passphrase
        byte[] seed = passphrase.getBytes(StandardCharsets.UTF_8);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE, random);
        KeyPair keyPair = keyGen.generateKeyPair();

        keyCache.put(passphrase, keyPair);
        return keyPair;
    }

    public int getRequiredKeyLength() {
        return 4;
    }

    public String getAlgorithmName() {
        return "RSA";
    }
}