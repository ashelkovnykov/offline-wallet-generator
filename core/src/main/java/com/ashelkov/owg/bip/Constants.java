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

    // Chia wallets use the BIP-44 'purpose' field in their own way:
    // https://github.com/Chia-Network/chia-blockchain/blob/34edf6e35e7126afb124037f8a6bb5feda833c32/chia/wallet/derive_keys.py#L30
    public static final int BLS_12_381_PURPOSE = 12381;

    // Checksums for all coins use only the first 4 bytes
    public static final int CHECKSUM_LENGTH = 4;

    private Constants() {}
}
