package com.ashelkov.owg.bip;

public class Constants {

    // Hardened keys have index >= 0x80000000:
    // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
    public static final int HARDENED = 0x80000000;

    // All generated wallets are BIP-44 compliant:
    // https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
    public static final int BIP44_PURPOSE = 44;

    // Bitcoin and Litecoin wallets are BIP-84 compliant:
    // https://github.com/bitcoin/bips/blob/master/bip-0084.mediawiki
    public static final int BIP84_PURPOSE = 84;

    private Constants() {}
}
