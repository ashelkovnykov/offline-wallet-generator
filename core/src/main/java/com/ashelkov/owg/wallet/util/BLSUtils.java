package com.ashelkov.owg.wallet.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;

public class BLSUtils {

    private static final Logger logger = LoggerFactory.getLogger(BLSUtils.class);
    private static final String HMAC_SHA_256_ALGORITHM = "HmacSHA256";
    // All of the constants below specified here:
    // https://eips.ethereum.org/EIPS/eip-2333#key-derivation
    private static final String SALT = "BLS-SIG-KEYGEN-SALT-";
    private static final BigInteger ORDER =
            new BigInteger("52435875175126190479447740508185965837690552500527637822603658699938581184513");
    private static final int HASH_LEN = 32;
    private static final int NUM_LAMPORT_HASH = 255;
    private static final byte L = (byte)48;

    /**
     * https://en.wikipedia.org/wiki/HMAC
     * https://github.com/relic-toolkit/relic/blob/69c51954c3ce458fc6ede3dca6d2e53817e38f6b/src/md/relic_md_hmac.c
     */
    private static byte[] extractAndExpand(byte[] input, byte[] key, byte[] info, int outputBytes) {

        byte[] output = new byte[outputBytes];
        int iterations = (outputBytes + HASH_LEN - 1) / HASH_LEN;
        int omh = outputBytes % HASH_LEN;
        int finalWrite = (omh == 0) ? HASH_LEN : omh;

        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256_ALGORITHM);

            final byte[] prk = new byte[HASH_LEN];
            mac.init(new SecretKeySpec(key, HMAC_SHA_256_ALGORITHM));
            mac.update(input);
            mac.doFinal(prk, 0);

//            logger.error("");
//            logger.error(EncodingUtils.bytesToHex(key));
//            logger.error(EncodingUtils.bytesToHex(input));
//            if (info != null) {
//                logger.error(EncodingUtils.bytesToHex(info));
//            }
//            logger.error(EncodingUtils.bytesToHex(prk));

            byte[] storage = new byte[HASH_LEN];
            mac.init(new SecretKeySpec(prk, HMAC_SHA_256_ALGORITHM));
            for (int i = 1; i <= iterations; ++i) {

                mac.reset();
                if (i != 1) {
                    mac.update(storage);
                }
                mac.update(info);
                mac.update((byte)i);

                mac.doFinal(storage, 0);
//                logger.error(EncodingUtils.bytesToHex(storage));
                if (i == iterations) {
                    System.arraycopy(storage, 0, output, ((i - 1) * HASH_LEN), finalWrite);
                } else {
                    System.arraycopy(storage, 0, output, ((i - 1) * HASH_LEN), HASH_LEN);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

//        logger.error(EncodingUtils.bytesToHex(output));

        return output;
    }

    /**
     *
     * As of version 4 of IETF BLS, as specified by the
     * [IRTF CFRG BLS Standard](https://datatracker.ietf.org/doc/draft-irtf-cfrg-bls-signature/04/), a null child key
     * (one for which `SK = OS2IP(OKM) mod r = 0`) is invalid. Note that this is the version used by
     * [EIP-2333](https://eips.ethereum.org/EIPS/eip-2333). Chia uses
     * [version 2](https://www.ietf.org/archive/id/draft-irtf-cfrg-bls-signature-02.txt), which treats the null child
     * key as valid (see https://docs.chia.net/docs/09keys/keys-and-signatures/#difference-between-chia-and-eip-2333).
     *
     * @param lamportKey
     * @return
     */
    private static byte[] generateKey(byte[] lamportKey) {

        byte[] input = new byte[lamportKey.length + 1];
        System.arraycopy(lamportKey,0, input,0, lamportKey.length);

        byte[] info = new byte[2];
        info[1] = L;

        byte[] rawBytes = extractAndExpand(input, SALT.getBytes(StandardCharsets.US_ASCII), info, L);
        byte[] positiveBytes = new byte[L + 1];
        positiveBytes[0] = 0x00;
        System.arraycopy(rawBytes, 0, positiveBytes, 1, L);

        BigInteger rawKey = new BigInteger(positiveBytes);
        BigInteger modKey = rawKey.mod(ORDER);
        byte[] finalBytes = modKey.toByteArray();

        logger.error("");
        logger.error(EncodingUtils.bytesToHex(rawBytes));
        logger.error(rawKey.toString());
        logger.error(modKey.toString());
        logger.error(EncodingUtils.bytesToHex(finalBytes));

        return finalBytes;
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
        byte[] lamportRaw = new byte[NUM_LAMPORT_HASH * HASH_LEN * 2];

        byte[] hashInput = new byte[32];
        byte[] hashOutput;
        for (int i = 0; i < NUM_LAMPORT_HASH; ++i) {
            // left
            System.arraycopy(lamportRawLeft, (i * HASH_LEN), hashInput, 0, HASH_LEN);
            hashOutput = Hash.sha256(hashInput);// TODO: 2
            System.arraycopy(hashOutput, 0, lamportRaw, (i * HASH_LEN), HASH_LEN);

            // right
            System.arraycopy(lamportRawRight, (i * HASH_LEN), hashInput, 0, HASH_LEN);
            hashOutput = Hash.sha256(hashInput);
            System.arraycopy(hashOutput, 0, lamportRaw, ((i * HASH_LEN) + (NUM_LAMPORT_HASH * HASH_LEN)), HASH_LEN);
        }

        return Hash.sha256(lamportRaw);
    }

    private static byte[] toLamportRaw(byte[] key, byte[] salt) {
        return extractAndExpand(key, salt, null, (NUM_LAMPORT_HASH * HASH_LEN));
    }

    public static byte[] deriveBip44ChildKey(byte[] seed, int[] path) {
        byte[] next = seed;
        for (int index: path) {
            next = deriveChildKey(next, index);
        }
        return next;
    }

    public static byte[] deriveChildKey(byte[] parentKey, int index) {
        return generateKey(toLamportKey(parentKey, index));
    }

    public static byte[] deriveParentKey(byte[] seed) {
        return generateKey(seed);
    }

    private BLSUtils() {}
}
