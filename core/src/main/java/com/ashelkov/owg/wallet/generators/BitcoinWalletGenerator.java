package com.ashelkov.owg.wallet.generators;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.wallet.BitcoinWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class BitcoinWalletGenerator extends WalletGenerator {

    private static final String BECH32_HRP = "bc";
    private static final byte WITNESS_VERSION = (byte)0x00;
    private static final int XPUB_VERSION = 0x04b24746;
    // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Serialization_format
    private static final int XPUB_CORE_LENGTH = 78;
    private static final int XPUB_CHECKSUM_LENGTH = 4;

    private final Bip32ECKeyPair masterKeyPair;

    public BitcoinWalletGenerator(byte[] seed) {
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, BitcoinWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, BitcoinWallet.COIN);
    }

    @Override
    public BitcoinWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = DEFAULT_FIELD_VAL;
        }
        if (change == null) {
            logMissing(CHANGE);
            change = DEFAULT_FIELD_VAL;
        }
        if (index == null) {
            logMissing(INDEX);
            index = DEFAULT_FIELD_VAL;
        }

        BIP84Address masterPubKey = generateAccountPubKey(account);

        List<BIP44Address> derivedAddresses = new ArrayList<>(numAddresses);
        for(int i = index; i < (index + numAddresses); ++i) {
            derivedAddresses.add(generateDerivedAddress(account, change, i));
        }

        return new BitcoinWallet(masterPubKey, derivedAddresses);
    }

    @Override
    public BitcoinWallet generateDefaultWallet() {

        BIP84Address masterPubKey = generateAccountPubKey(DEFAULT_FIELD_VAL);
        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateDerivedAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new BitcoinWallet(masterPubKey, wrapper);
    }

    private BIP84Address generateAccountPubKey(int account) {

        int[] addressPath = getAccountAddressPath(account);
        Bip32ECKeyPair accountKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        ByteBuffer xpubKeyBuilder = ByteBuffer.allocate(XPUB_CORE_LENGTH);
        xpubKeyBuilder.putInt(XPUB_VERSION);
        xpubKeyBuilder.put((byte)(accountKeyPair.getDepth()));
        xpubKeyBuilder.putInt(accountKeyPair.getParentFingerprint());
        xpubKeyBuilder.putInt(addressPath[2]);
        xpubKeyBuilder.put(accountKeyPair.getChainCode());
        xpubKeyBuilder.put(accountKeyPair.getPublicKeyPoint().getEncoded(true));
        byte[] xpubKeyCore = xpubKeyBuilder.array();
        byte[] checksum = Hash.sha256(Hash.sha256(xpubKeyCore));

        byte[] xpubKey = new byte[XPUB_CORE_LENGTH + XPUB_CHECKSUM_LENGTH];
        System.arraycopy(xpubKeyCore, 0, xpubKey, 0, XPUB_CORE_LENGTH);
        System.arraycopy(checksum, 0, xpubKey, XPUB_CORE_LENGTH, XPUB_CHECKSUM_LENGTH);

        String xpubKeySerialized = EncodingUtils.base58Bitcoin(xpubKey);

        return new BIP84Address(xpubKeySerialized, addressPath);
    }

    private int[] getAccountAddressPath(int account) {
        int purpose = BitcoinWallet.PURPOSE | HARDENED;
        int coinCode = BitcoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }

    private BIP84Address generateDerivedAddress(int account, int change, int index) {

        int[] addressPath = getDerivedAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        byte[] unencodedAddress = EncodingUtils.to5BitBytesSafe(
                Hash.sha256hash160(
                        derivedKeyPair
                                .getPublicKeyPoint()
                                .getEncoded(true)));
        byte[] unencodedAddressWithWitness = new byte[unencodedAddress.length + 1];
        unencodedAddressWithWitness[0] = WITNESS_VERSION;
        System.arraycopy(unencodedAddress, 0, unencodedAddressWithWitness, 1, unencodedAddress.length);

        String address = Bech32.encode(BECH32_HRP, unencodedAddressWithWitness);

        return new BIP84Address(address, addressPath);
    }

    private int[] getDerivedAddressPath(int account, int change, int index) {
        int purpose = BitcoinWallet.PURPOSE | HARDENED;
        int coinCode = BitcoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
