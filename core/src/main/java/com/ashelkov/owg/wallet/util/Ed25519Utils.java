package com.ashelkov.owg.wallet.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ed25519Utils {

    private static final Logger logger = LoggerFactory.getLogger(Ed25519Utils.class);
    private static final String HMAC_SHA_512_ALGORITHM = "HmacSHA512";

    private static int load_3(byte[] in, int offset) {
        int result = in[offset++] & 0xff;
        result |= (in[offset++] & 0xff) << 8;
        result |= (in[offset] & 0xff) << 16;
        return result;
    }

    private static long load_4(byte[] in, int offset) {
        int result = in[offset++] & 0xff;
        result |= (in[offset++] & 0xff) << 8;
        result |= (in[offset++] & 0xff) << 16;
        result |= in[offset] << 24;
        return ((long)result) & 0xffffffffL;
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

    /**
     * https://github.com/str4d/ed25519-java/blob/master/src/net/i2p/crypto/eddsa/math/ed25519/Ed25519ScalarOps.java
     *
     * ==========================================================================================================
     *
     * Reduction modulo the group order $q$.
     * <p>
     * Input:
     *   $s[0]+256*s[1]+\dots+256^{63}*s[63] = s$
     * <p>
     * Output:
     *   $s[0]+256*s[1]+\dots+256^{31}*s[31] = s \bmod q$
     *   where $q = 2^{252} + 27742317777372353535851937790883648493$.
     */
    public static byte[] reduce(byte[] s) {
        // s0,..., s22 have 21 bits, s23 has 29 bits
        long s0 = 0x1FFFFF & load_3(s, 0);
        long s1 = 0x1FFFFF & (load_4(s, 2) >> 5);
        long s2 = 0x1FFFFF & (load_3(s, 5) >> 2);
        long s3 = 0x1FFFFF & (load_4(s, 7) >> 7);
        long s4 = 0x1FFFFF & (load_4(s, 10) >> 4);
        long s5 = 0x1FFFFF & (load_3(s, 13) >> 1);
        long s6 = 0x1FFFFF & (load_4(s, 15) >> 6);
        long s7 = 0x1FFFFF & (load_3(s, 18) >> 3);
        long s8 = 0x1FFFFF & load_3(s, 21);
        long s9 = 0x1FFFFF & (load_4(s, 23) >> 5);
        long s10 = 0x1FFFFF & (load_3(s, 26) >> 2);
        long s11 = 0x1FFFFF & (load_4(s, 28) >> 7);
        long s12 = 0x1FFFFF & (load_4(s, 31) >> 4);
        long s13 = 0x1FFFFF & (load_3(s, 34) >> 1);
        long s14 = 0x1FFFFF & (load_4(s, 36) >> 6);
        long s15 = 0x1FFFFF & (load_3(s, 39) >> 3);
        long s16 = 0x1FFFFF & load_3(s, 42);
        long s17 = 0x1FFFFF & (load_4(s, 44) >> 5);
        long s18 = 0x1FFFFF & (load_3(s, 47) >> 2);
        long s19 = 0x1FFFFF & (load_4(s, 49) >> 7);
        long s20 = 0x1FFFFF & (load_4(s, 52) >> 4);
        long s21 = 0x1FFFFF & (load_3(s, 55) >> 1);
        long s22 = 0x1FFFFF & (load_4(s, 57) >> 6);
        long s23 = (load_4(s, 60) >> 3);
        long carry0;
        long carry1;
        long carry2;
        long carry3;
        long carry4;
        long carry5;
        long carry6;
        long carry7;
        long carry8;
        long carry9;
        long carry10;
        long carry11;
        long carry12;
        long carry13;
        long carry14;
        long carry15;
        long carry16;

        /*
         * Lots of magic numbers :)
         * To understand what's going on below, note that
         *
         * (1) q = 2^252 + q0 where q0 = 27742317777372353535851937790883648493.
         * (2) s11 is the coefficient of 2^(11*21), s23 is the coefficient of 2^(^23*21) and 2^252 = 2^((23-11) * 21)).
         * (3) 2^252 congruent -q0 modulo q.
         * (4) -q0 = 666643 * 2^0 + 470296 * 2^21 + 654183 * 2^(2*21) - 997805 * 2^(3*21) + 136657 * 2^(4*21) - 683901 * 2^(5*21)
         *
         * Thus
         * s23 * 2^(23*11) = s23 * 2^(12*21) * 2^(11*21) = s3 * 2^252 * 2^(11*21) congruent
         * s23 * (666643 * 2^0 + 470296 * 2^21 + 654183 * 2^(2*21) - 997805 * 2^(3*21) + 136657 * 2^(4*21) - 683901 * 2^(5*21)) * 2^(11*21) modulo q =
         * s23 * (666643 * 2^(11*21) + 470296 * 2^(12*21) + 654183 * 2^(13*21) - 997805 * 2^(14*21) + 136657 * 2^(15*21) - 683901 * 2^(16*21)).
         *
         * The same procedure is then applied for s22,...,s18.
         */
        s11 += s23 * 666643;
        s12 += s23 * 470296;
        s13 += s23 * 654183;
        s14 -= s23 * 997805;
        s15 += s23 * 136657;
        s16 -= s23 * 683901;
        // not used again
        //s23 = 0;

        s10 += s22 * 666643;
        s11 += s22 * 470296;
        s12 += s22 * 654183;
        s13 -= s22 * 997805;
        s14 += s22 * 136657;
        s15 -= s22 * 683901;
        // not used again
        //s22 = 0;

        s9 += s21 * 666643;
        s10 += s21 * 470296;
        s11 += s21 * 654183;
        s12 -= s21 * 997805;
        s13 += s21 * 136657;
        s14 -= s21 * 683901;
        // not used again
        //s21 = 0;

        s8 += s20 * 666643;
        s9 += s20 * 470296;
        s10 += s20 * 654183;
        s11 -= s20 * 997805;
        s12 += s20 * 136657;
        s13 -= s20 * 683901;
        // not used again
        //s20 = 0;

        s7 += s19 * 666643;
        s8 += s19 * 470296;
        s9 += s19 * 654183;
        s10 -= s19 * 997805;
        s11 += s19 * 136657;
        s12 -= s19 * 683901;
        // not used again
        //s19 = 0;

        s6 += s18 * 666643;
        s7 += s18 * 470296;
        s8 += s18 * 654183;
        s9 -= s18 * 997805;
        s10 += s18 * 136657;
        s11 -= s18 * 683901;
        // not used again
        //s18 = 0;

        /*
         * Time to reduce the coefficient in order not to get an overflow.
         */
        carry6 = (s6 + (1<<20)) >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry8 = (s8 + (1<<20)) >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry10 = (s10 + (1<<20)) >> 21; s11 += carry10; s10 -= carry10 << 21;
        carry12 = (s12 + (1<<20)) >> 21; s13 += carry12; s12 -= carry12 << 21;
        carry14 = (s14 + (1<<20)) >> 21; s15 += carry14; s14 -= carry14 << 21;
        carry16 = (s16 + (1<<20)) >> 21; s17 += carry16; s16 -= carry16 << 21;

        carry7 = (s7 + (1<<20)) >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry9 = (s9 + (1<<20)) >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry11 = (s11 + (1<<20)) >> 21; s12 += carry11; s11 -= carry11 << 21;
        carry13 = (s13 + (1<<20)) >> 21; s14 += carry13; s13 -= carry13 << 21;
        carry15 = (s15 + (1<<20)) >> 21; s16 += carry15; s15 -= carry15 << 21;

        /*
         * Continue with above procedure.
         */
        s5 += s17 * 666643;
        s6 += s17 * 470296;
        s7 += s17 * 654183;
        s8 -= s17 * 997805;
        s9 += s17 * 136657;
        s10 -= s17 * 683901;
        // not used again
        //s17 = 0;

        s4 += s16 * 666643;
        s5 += s16 * 470296;
        s6 += s16 * 654183;
        s7 -= s16 * 997805;
        s8 += s16 * 136657;
        s9 -= s16 * 683901;
        // not used again
        //s16 = 0;

        s3 += s15 * 666643;
        s4 += s15 * 470296;
        s5 += s15 * 654183;
        s6 -= s15 * 997805;
        s7 += s15 * 136657;
        s8 -= s15 * 683901;
        // not used again
        //s15 = 0;

        s2 += s14 * 666643;
        s3 += s14 * 470296;
        s4 += s14 * 654183;
        s5 -= s14 * 997805;
        s6 += s14 * 136657;
        s7 -= s14 * 683901;
        // not used again
        //s14 = 0;

        s1 += s13 * 666643;
        s2 += s13 * 470296;
        s3 += s13 * 654183;
        s4 -= s13 * 997805;
        s5 += s13 * 136657;
        s6 -= s13 * 683901;
        // not used again
        //s13 = 0;

        s0 += s12 * 666643;
        s1 += s12 * 470296;
        s2 += s12 * 654183;
        s3 -= s12 * 997805;
        s4 += s12 * 136657;
        s5 -= s12 * 683901;
        // set below
        //s12 = 0;

        /*
         * Reduce coefficients again.
         */
        carry0 = (s0 + (1<<20)) >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry2 = (s2 + (1<<20)) >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry4 = (s4 + (1<<20)) >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry6 = (s6 + (1<<20)) >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry8 = (s8 + (1<<20)) >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry10 = (s10 + (1<<20)) >> 21; s11 += carry10; s10 -= carry10 << 21;

        carry1 = (s1 + (1<<20)) >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry3 = (s3 + (1<<20)) >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry5 = (s5 + (1<<20)) >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry7 = (s7 + (1<<20)) >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry9 = (s9 + (1<<20)) >> 21; s10 += carry9; s9 -= carry9 << 21;
        //carry11 = (s11 + (1<<20)) >> 21; s12 += carry11; s11 -= carry11 << 21;
        carry11 = (s11 + (1<<20)) >> 21; s12 = carry11; s11 -= carry11 << 21;

        s0 += s12 * 666643;
        s1 += s12 * 470296;
        s2 += s12 * 654183;
        s3 -= s12 * 997805;
        s4 += s12 * 136657;
        s5 -= s12 * 683901;
        // set below
        //s12 = 0;

        carry0 = s0 >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry1 = s1 >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry2 = s2 >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry3 = s3 >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry4 = s4 >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry5 = s5 >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry6 = s6 >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry7 = s7 >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry8 = s8 >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry9 = s9 >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry10 = s10 >> 21; s11 += carry10; s10 -= carry10 << 21;
        //carry11 = s11 >> 21; s12 += carry11; s11 -= carry11 << 21;
        carry11 = s11 >> 21; s12 = carry11; s11 -= carry11 << 21;

        // TODO-CR BR: Is it really needed to do it TWO times? (it doesn't hurt, just a question).
        s0 += s12 * 666643;
        s1 += s12 * 470296;
        s2 += s12 * 654183;
        s3 -= s12 * 997805;
        s4 += s12 * 136657;
        s5 -= s12 * 683901;
        // not used again
        //s12 = 0;

        carry0 = s0 >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry1 = s1 >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry2 = s2 >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry3 = s3 >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry4 = s4 >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry5 = s5 >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry6 = s6 >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry7 = s7 >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry8 = s8 >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry9 = s9 >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry10 = s10 >> 21; s11 += carry10; s10 -= carry10 << 21;

        // s0, ..., s11 got 21 bits each.
        byte[] result = new byte[32];
        result[0] = (byte) s0;
        result[1] = (byte) (s0 >> 8);
        result[2] = (byte) ((s0 >> 16) | (s1 << 5));
        result[3] = (byte) (s1 >> 3);
        result[4] = (byte) (s1 >> 11);
        result[5] = (byte) ((s1 >> 19) | (s2 << 2));
        result[6] = (byte) (s2 >> 6);
        result[7] = (byte) ((s2 >> 14) | (s3 << 7));
        result[8] = (byte) (s3 >> 1);
        result[9] = (byte) (s3 >> 9);
        result[10] = (byte) ((s3 >> 17) | (s4 << 4));
        result[11] = (byte) (s4 >> 4);
        result[12] = (byte) (s4 >> 12);
        result[13] = (byte) ((s4 >> 20) | (s5 << 1));
        result[14] = (byte) (s5 >> 7);
        result[15] = (byte) ((s5 >> 15) | (s6 << 6));
        result[16] = (byte) (s6 >> 2);
        result[17] = (byte) (s6 >> 10);
        result[18] = (byte) ((s6 >> 18) | (s7 << 3));
        result[19] = (byte) (s7 >> 5);
        result[20] = (byte) (s7 >> 13);
        result[21] = (byte) s8;
        result[22] = (byte) (s8 >> 8);
        result[23] = (byte) ((s8 >> 16) | (s9 << 5));
        result[24] = (byte) (s9 >> 3);
        result[25] = (byte) (s9 >> 11);
        result[26] = (byte) ((s9 >> 19) | (s10 << 2));
        result[27] = (byte) (s10 >> 6);
        result[28] = (byte) ((s10 >> 14) | (s11 << 7));
        result[29] = (byte) (s11 >> 1);
        result[30] = (byte) (s11 >> 9);
        result[31] = (byte) (s11 >> 17);
        return result;
    }

    /**
     * https://github.com/monero-project/monero/blob/dcba757dd283a3396120f0df90fe746e3ec02292/src/crypto/crypto-ops.c
     *
     * @param s
     * @return
     */
    public static byte[] reduce32(byte[] s) {
        long s0 = 0x1FFFFF & load_3(s, 0);
        long s1 = 0x1FFFFF & (load_4(s, 2) >> 5);
        long s2 = 0x1FFFFF & (load_3(s, 5) >> 2);
        long s3 = 0x1FFFFF & (load_4(s, 7) >> 7);
        long s4 = 0x1FFFFF & (load_4(s, 10) >> 4);
        long s5 = 0x1FFFFF & (load_3(s, 13) >> 1);
        long s6 = 0x1FFFFF & (load_4(s, 15) >> 6);
        long s7 = 0x1FFFFF & (load_3(s, 18) >> 3);
        long s8 = 0x1FFFFF & load_3(s, 21);
        long s9 = 0x1FFFFF & (load_4(s, 23) >> 5);
        long s10 = 0x1FFFFF & (load_3(s, 26) >> 2);
        long s11 = (load_4(s, 28) >> 7);
        long s12 = 0;
        long carry0;
        long carry1;
        long carry2;
        long carry3;
        long carry4;
        long carry5;
        long carry6;
        long carry7;
        long carry8;
        long carry9;
        long carry10;
        long carry11;

        carry0 = (s0 + (1<<20)) >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry2 = (s2 + (1<<20)) >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry4 = (s4 + (1<<20)) >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry6 = (s6 + (1<<20)) >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry8 = (s8 + (1<<20)) >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry10 = (s10 + (1<<20)) >> 21; s11 += carry10; s10 -= carry10 << 21;

        carry1 = (s1 + (1<<20)) >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry3 = (s3 + (1<<20)) >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry5 = (s5 + (1<<20)) >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry7 = (s7 + (1<<20)) >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry9 = (s9 + (1<<20)) >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry11 = (s11 + (1<<20)) >> 21; s12 += carry11; s11 -= carry11 << 21;

        s0 += s12 * 666643;
        s1 += s12 * 470296;
        s2 += s12 * 654183;
        s3 -= s12 * 997805;
        s4 += s12 * 136657;
        s5 -= s12 * 683901;
        s12 = 0;

        carry0 = s0 >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry1 = s1 >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry2 = s2 >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry3 = s3 >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry4 = s4 >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry5 = s5 >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry6 = s6 >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry7 = s7 >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry8 = s8 >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry9 = s9 >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry10 = s10 >> 21; s11 += carry10; s10 -= carry10 << 21;
        carry11 = s11 >> 21; s12 += carry11; s11 -= carry11 << 21;

        s0 += s12 * 666643;
        s1 += s12 * 470296;
        s2 += s12 * 654183;
        s3 -= s12 * 997805;
        s4 += s12 * 136657;
        s5 -= s12 * 683901;

        carry0 = s0 >> 21; s1 += carry0; s0 -= carry0 << 21;
        carry1 = s1 >> 21; s2 += carry1; s1 -= carry1 << 21;
        carry2 = s2 >> 21; s3 += carry2; s2 -= carry2 << 21;
        carry3 = s3 >> 21; s4 += carry3; s3 -= carry3 << 21;
        carry4 = s4 >> 21; s5 += carry4; s4 -= carry4 << 21;
        carry5 = s5 >> 21; s6 += carry5; s5 -= carry5 << 21;
        carry6 = s6 >> 21; s7 += carry6; s6 -= carry6 << 21;
        carry7 = s7 >> 21; s8 += carry7; s7 -= carry7 << 21;
        carry8 = s8 >> 21; s9 += carry8; s8 -= carry8 << 21;
        carry9 = s9 >> 21; s10 += carry9; s9 -= carry9 << 21;
        carry10 = s10 >> 21; s11 += carry10; s10 -= carry10 << 21;

        byte[] result = new byte[32];
        result[0] = (byte) s0;
        result[1] = (byte) (s0 >> 8);
        result[2] = (byte) ((s0 >> 16) | (s1 << 5));
        result[3] = (byte) (s1 >> 3);
        result[4] = (byte) (s1 >> 11);
        result[5] = (byte) ((s1 >> 19) | (s2 << 2));
        result[6] = (byte) (s2 >> 6);
        result[7] = (byte) ((s2 >> 14) | (s3 << 7));
        result[8] = (byte) (s3 >> 1);
        result[9] = (byte) (s3 >> 9);
        result[10] = (byte) ((s3 >> 17) | (s4 << 4));
        result[11] = (byte) (s4 >> 4);
        result[12] = (byte) (s4 >> 12);
        result[13] = (byte) ((s4 >> 20) | (s5 << 1));
        result[14] = (byte) (s5 >> 7);
        result[15] = (byte) ((s5 >> 15) | (s6 << 6));
        result[16] = (byte) (s6 >> 2);
        result[17] = (byte) (s6 >> 10);
        result[18] = (byte) ((s6 >> 18) | (s7 << 3));
        result[19] = (byte) (s7 >> 5);
        result[20] = (byte) (s7 >> 13);
        result[21] = (byte) s8;
        result[22] = (byte) (s8 >> 8);
        result[23] = (byte) ((s8 >> 16) | (s9 << 5));
        result[24] = (byte) (s9 >> 3);
        result[25] = (byte) (s9 >> 11);
        result[26] = (byte) ((s9 >> 19) | (s10 << 2));
        result[27] = (byte) (s10 >> 6);
        result[28] = (byte) ((s10 >> 14) | (s11 << 7));
        result[29] = (byte) (s11 >> 1);
        result[30] = (byte) (s11 >> 9);
        result[31] = (byte) (s11 >> 17);
        return result;
    }

    private Ed25519Utils() {}
}
