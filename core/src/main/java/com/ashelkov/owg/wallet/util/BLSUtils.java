package com.ashelkov.owg.wallet.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;

public class BLSUtils {

    private static final Logger logger = LoggerFactory.getLogger(BLSUtils.class);
    private static final String HMAC_SHA_256_ALGORITHM = "HmacSHA256";
    private static final int LAMPORT_ROUNDS = 255;
    private static final String SALT = "BLS-SIG-KEYGEN-SALT-";
    // L = ceil((3 * ceil(log2(r))) / 16), where r is the order of the BLS12-381 curve
    private static final byte L = (byte)48;

    public static final int HASH_LEN = 32;

    /**
     * https://en.wikipedia.org/wiki/HMAC
     * https://github.com/relic-toolkit/relic/blob/69c51954c3ce458fc6ede3dca6d2e53817e38f6b/src/md/relic_md_hmac.c
     */
    private static byte[] extractAndExpand(byte[] input, byte[] key, byte[] info) {

        byte[] output = new byte[LAMPORT_ROUNDS * HASH_LEN];

        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256_ALGORITHM);

            final byte[] prk = new byte[HASH_LEN];
            mac.init(new SecretKeySpec(key, HMAC_SHA_256_ALGORITHM));
            mac.update(input);
            mac.doFinal(prk, 0);

            for (int i = 0; i < LAMPORT_ROUNDS; ++i) {
                mac.init(new SecretKeySpec(prk, HMAC_SHA_256_ALGORITHM)); // TODO: maybe remove

                if (i != 0) {
                    mac.update(output, (i * HASH_LEN), HASH_LEN);
                }
                mac.update(info);
                mac.update((byte)(i + 1));

                mac.doFinal(output, (i * HASH_LEN));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        return output;
    }

    /**
     *
     * 
     * https://eips.ethereum.org/EIPS/eip-2333
     *
     * @param lamportKey
     * @return
     */
    private static byte[] generateKey(byte[] lamportKey) {

        byte[] input = new byte[HASH_LEN + 1];
        System.arraycopy(lamportKey,0,input,0,HASH_LEN);
        input[HASH_LEN] = 0;                // TODO: maybe remove

        byte[] info = new byte[2];
        info[0] = 0;                        // TODO: maybe remove
        info[1] = L;

        return extractAndExpand(input, SALT.getBytes(StandardCharsets.UTF_8), info);
    }

    private static byte[] toLamportKey(byte[] parentKey, int index) {

        assert(parentKey.length == HASH_LEN);

        byte[] salt = EncodingUtils.intToFourBytes(index);
        byte[] parentKeyInverted = new byte[parentKey.length];
        for (int i = 0; i < HASH_LEN; i++) {
            parentKeyInverted[i] = (byte)(parentKey[i] ^ 0xff);
        }

        byte[] lamportRawLeft = toLamportRaw(parentKey, salt);
        byte[] lamportRawRight = toLamportRaw(parentKeyInverted, salt);
        byte[] lamportRaw = new byte[LAMPORT_ROUNDS * HASH_LEN * 2];

        byte[] hashInput = new byte[32];
        byte[] hashOutput;
        for (int i = 0; i < 255; ++i) {
            // left
            System.arraycopy(lamportRawLeft, (i * HASH_LEN), hashInput, 0, HASH_LEN);
            hashOutput = Hash.sha256(hashInput);
            System.arraycopy(hashOutput, 0, lamportRaw, (i * HASH_LEN), HASH_LEN);

            // right
            System.arraycopy(lamportRawRight, (i * HASH_LEN), hashInput, 0, HASH_LEN);
            hashOutput = Hash.sha256(hashInput);
            System.arraycopy(hashOutput, 0, lamportRaw, ((i * HASH_LEN) + (LAMPORT_ROUNDS * HASH_LEN)), HASH_LEN);
        }

        return Hash.sha256(lamportRaw);
    }

    private static byte[] toLamportRaw(byte[] key, byte[] salt) {
        return extractAndExpand(key, salt, null);
    }

    public static byte[] deriveChildSk(byte[] parentKey, int index) {
        return generateKey(toLamportKey(parentKey, index));
    }

    private BLSUtils() {}
}
