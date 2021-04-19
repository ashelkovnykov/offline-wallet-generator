package com.ashelkov.wallet.bip.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.wallet.bip.Constants;

public class Bip44Utils {

    private static final Logger logger = LoggerFactory.getLogger(Bip44Utils.class);
    private static final String HMAC_SHA_512_ALGORITHM = "HmacSHA512";

    private Bip44Utils() {}

    public static String convertPathToText(int[] path) {

        StringBuilder result = new StringBuilder();

        result.append("m/");

        for (int chainVal : path) {
            // Hardened
            if ((chainVal & Constants.HARDENED) != 0) {
                int val = chainVal ^ Constants.HARDENED;

                result.append(val);
                result.append('\'');

            // Not hardened
            } else {
                result.append(chainVal);
            }

            result.append('/');
        }

        result.setLength(result.length() - 1);

        return result.toString();
    }

    /**
     * NOTE: Code copied from...
     * https://github.com/stellar/java-stellar-sdk/blob/ce7de4f074bc16416b537e583bf06227315b879c/src/main/java/org/stellar/sdk/SLIP10.java#L30
     *
     * ================================================================================================================
     *
     * Derives only the private key for ED25519 in the manor defined in
     * <a href="https://github.com/satoshilabs/slips/blob/master/slip-0010.md">SLIP-0010</a>.
     *
     * @param seed    Seed, the BIP0039 output.
     * @param indexes an array of indexes that define the path. E.g. for m/1'/2'/3', pass 1, 2, 3.
     *                As with Ed25519 non-hardened child indexes are not supported, this function treats all indexes
     *                as hardened.
     * @return Private key.
     */
    public static byte[] deriveEd25519PrivateKey(final byte[] seed, final int... indexes) {

        final byte[] Il = new byte[32];

        try {
            final byte[] I = new byte[64];
            final Mac mac = Mac.getInstance(HMAC_SHA_512_ALGORITHM);

            // I = HMAC-SHA512(Key = bytes("ed25519 seed"), Data = seed)
            mac.init(new SecretKeySpec("ed25519 seed".getBytes(StandardCharsets.UTF_8), HMAC_SHA_512_ALGORITHM));
            mac.update(seed);
            mac.doFinal(I, 0);

            for (int i : indexes) {
                // I = HMAC-SHA512(Key = c_par, Data = 0x00 || ser256(k_par) || ser32(i'))
                // which is simply:
                // I = HMAC-SHA512(Key = Ir, Data = 0x00 || Il || ser32(i'))
                // Key = Ir
                mac.init(new SecretKeySpec(I, 32, 32, HMAC_SHA_512_ALGORITHM));
                // Data = 0x00
                mac.update((byte) 0x00);
                // Data += Il
                mac.update(I, 0, 32);
                // Data += ser32(i')
                mac.update((byte) (i >> 24 | 0x80));
                mac.update((byte) (i >> 16));
                mac.update((byte) (i >> 8));
                mac.update((byte) i);
                // Write to I
                mac.doFinal(I, 0);
            }

            // copy head 32 bytes of I into Il
            System.arraycopy(I, 0, Il, 0, 32);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        return Il;
    }
}
