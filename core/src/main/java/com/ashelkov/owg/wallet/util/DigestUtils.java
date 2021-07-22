package com.ashelkov.owg.wallet.util;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Copied from Algorand com.algorand.algosdk.util.Digester and com.algorand.algosdk.util.CryptoProvider
 */
public class DigestUtils {

    // Used to select hash/digest algorithm from provider
    public static final String SHA_512_256 = "SHA-512/256";
    public static final String KECCAK_256 = "Keccak-256";

    private static void setupIfNeeded() {
        // Add bouncy castle provider for crypto, if it's not already added
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static byte[] digest(String alg, byte[] data) throws NoSuchAlgorithmException {

        setupIfNeeded();

        java.security.MessageDigest digest = java.security.MessageDigest.getInstance(alg);
        digest.update(Arrays.copyOf(data, data.length));

        return digest.digest();
    }
}
