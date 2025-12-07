package com.syscrypt.crypto;

public class CryptoManager {

    public enum Algorithm {
        AES("AES"),
        DES("DES"),
        RSA("RSA"),
        BLOWFISH("Blowfish");

        private final String displayName;

        Algorithm(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Algorithm fromDisplayName(String name) {
            for (Algorithm alg: values()) {
                if (alg.displayName.equalsIgnoreCase(name)) {
                    return alg;
                }
            }
            return AES; // Default
        }
    }

    private final AESCrypto aesCrypto = new AESCrypto();
    private final DESCrypto desCrypto = new DESCrypto();
    private final RSACrypto rsaCrypto = new RSACrypto();
    private final BlowfishCrypto blowfishCrypto = new BlowfishCrypto();

    public CryptoEngine getEngine(Algorithm algorithm) {
        switch (algorithm) {
            case DES:
                return desCrypto;
            case RSA:
                return rsaCrypto;
            case BLOWFISH:
                return blowfishCrypto;
            case AES:
            default:
                return aesCrypto;
        }
    }

    // Encrypts text using specified algorithm
    public String encrypt(String plainText, String key, Algorithm algorithm) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        CryptoEngine engine = getEngine(algorithm);
        if (key.length() < engine.getRequiredKeyLength()) {
            throw new IllegalArgumentException("Key must be at least " +
                    engine.getRequiredKeyLength() + " characters for " + algorithm.getDisplayName());
        }
        return engine.encrypt(plainText, key);
    }

    // Decrypts text using specified algorithm
    public String decrypt(String cipherText, String key, Algorithm algorithm) throws Exception {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Cipher text cannot be empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        CryptoEngine engine = getEngine(algorithm);
        return engine.decrypt(cipherText, key);
    }

    // Returns all algorithms for a dropdown
    public static String[] getAlgorithmNames() {
        Algorithm[] algorithms = Algorithm.values();
        String[] names = new String[algorithms.length];
        for (int i = 0; i < algorithms.length; i++) {
            names[i] = algorithms[i].getDisplayName();
        }
        return names;
    }
}