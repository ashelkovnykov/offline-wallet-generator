package com.ashelkov.owg.bip;

/**
 * Collection of constants used by all modern coin wallets.
 */
public final class Constants {

    // Hardened keys have index >= 0x80000000:
    // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
    public static final int HARDENED = 0x80000000;

    // Standard checksum length, whether using starting or trailing bytes
    public static final int CHECKSUM_LENGTH = 4;

    private Constants() {}
}
