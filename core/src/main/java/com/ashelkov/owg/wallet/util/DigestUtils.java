package com.ashelkov.owg.wallet.util;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * NOTE: This code is copied from Algorand SDK cource classes com.algorand.algosdk.util.Digester and
 *       com.algorand.algosdk.util.CryptoProvider.
 *
 * ====================================================================================================================
 *
 * Utilities for performing hashes.
 */
public class DigestUtils {

    // Used to select hash/digest algorithm from provider
    public static final String BLAKE2B_160 = "BLAKE2B-160";
    public static final String KECCAK_256 = "Keccak-256";
    public static final String SHA_512_256 = "SHA-512/256";

    /**
     * Add a hash function provider if one is not yet present.
     */
    private static void setupIfNeeded() {
        // Add bouncy castle provider for crypto, if it's not already added
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Hash given input data using the chosen named algorithm.
     *
     * @param alg Algorithm to use
     * @param data Input data to hash
     * @return Hash output
     * @throws NoSuchAlgorithmException if named hash function not found
     */
    public static byte[] digest(String alg, byte[] data) throws NoSuchAlgorithmException {

        setupIfNeeded();

        java.security.MessageDigest digest = java.security.MessageDigest.getInstance(alg);
        digest.update(Arrays.copyOf(data, data.length));

        return digest.digest();
    }

    /**
     * Hash given input data using the chosen named algorithm, but return an empty array if a problem occurs while
     * hashing.
     *
     * @param alg Algorithm to use
     * @param data Input data to hash
     * @return Hash output
     */
    public static byte[] unsafeDigest(String alg, byte[] data) {

        byte[] encodedData;
        try {
            encodedData = digest(alg, data);
        } catch (Exception e) {
            encodedData = new byte[0];
        }

        return encodedData;
    }
}
