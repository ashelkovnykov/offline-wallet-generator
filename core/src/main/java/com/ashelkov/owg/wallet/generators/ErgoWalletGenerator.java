package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.ErgoWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class ErgoWalletGenerator extends WalletGenerator {

    // First 4 bits:
    // 0x00 = Mainnet
    // 0x10 = Testnet
    //
    // Second 4 bits:
    // 0x01 = Pay-to-Public-Key (P2PK)
    // 0x02 = Pay-to-Script-Hash (P2SH)
    // 0x03 = Pay-to-Script (P2S)
    private static final byte PREFIX_BYTE = (byte)0x01;

    private final Bip32ECKeyPair masterKeyPair;

    public ErgoWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, ErgoWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, ErgoWallet.COIN);
    }

    @Override
    public ErgoWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new ErgoWallet(wrapper);
    }

    @Override
    public ErgoWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account != null) {
            logWarning(ACCOUNT, account);
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index == null) {
            logMissing(INDEX);
            index = DEFAULT_FIELD_VAL;
        }

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new ErgoWallet(addresses);
    }

    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        byte[] addressPreChecksum = new byte[34];
        byte[] addressWithChecksum = new byte[38];

        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        addressPreChecksum[0] = PREFIX_BYTE;
        System.arraycopy(
            derivedKeyPair.getPublicKeyPoint().getEncoded(true),
            0,
            addressPreChecksum,
            1,
            33);
        System.arraycopy(
            addressPreChecksum,
            0,
            addressWithChecksum,
            0,
            34);
        System.arraycopy(
            Hash.blake2b256(addressPreChecksum),
            0,
            addressWithChecksum,
            34,
            4);

        String address = EncodingUtils.base58Bitcoin(addressWithChecksum);
        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPrivateKeyBytes33()).substring(2);
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        }

        return new BIP44Address(address, addressPath, privKeyText, pubKeyText);
    }

    private int[] getAddressPath(int index) {
        int purpose = ErgoWallet.PURPOSE | HARDENED;
        int coinCode = ErgoWallet.COIN.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
