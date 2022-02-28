package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.wallet.util.Ed25519Utils;
import com.ashelkov.owg.wallet.util.EncodingUtils;
import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.XRPWallet;

import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;

public class XRPWalletGenerator extends WalletGenerator {

    private static final byte MASTER_PUB_KEY_PREFIX = (byte)0xED;
    private static final byte PAYLOAD_PREFIX = (byte)0x00;

    private final Bip32ECKeyPair masterKeyPair;
    private final byte[] seed;

    public XRPWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
        this.seed = seed;
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, XRPWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, XRPWallet.COIN);
    }

    @Override
    public XRPWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = DEFAULT_FIELD_VAL;
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index != null) {
            logWarning(INDEX, index);
        }

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new XRPWallet(addresses);
    }

    @Override
    public XRPWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new XRPWallet(wrapper);
    }

    /**
     *
     * https://xrpl.org/accounts.html#address-encoding
     *
     * @param account
     * @return
     */
    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);

        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        byte[] accountID = Hash.sha256hash160(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        byte[] payload = new byte[21];
        System.arraycopy(accountID, 0, payload, 1, 20);
        payload[0] = PAYLOAD_PREFIX;

        byte[] checksum = Hash.sha256(Hash.sha256(payload));
        byte[] rawAddress = new byte[25];
        System.arraycopy(payload, 0, rawAddress, 0, 21);
        System.arraycopy(checksum, 0, rawAddress, 21, CHECKSUM_LENGTH);
        String address = EncodingUtils.base58Ripple(rawAddress);

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

    private BIP44Address generateAddressED25519(int account) {

        int[] addressPathED25519 = getAddressPathED25519(account);

        byte[] rawPublicKey = KeyPair
                .fromSecretSeed(Ed25519Utils.deriveEd25519PrivateKey(seed, addressPathED25519))
                .getPublicKey();
        byte[] masterPublicKey = new byte[33];
        System.arraycopy(rawPublicKey, 0, masterPublicKey, 1, 32);
        masterPublicKey[0] = MASTER_PUB_KEY_PREFIX;

        byte[] accountID = Hash.sha256hash160(masterPublicKey);
        byte[] payload = new byte[21];
        System.arraycopy(accountID, 0, payload, 1, 20);
        payload[0] = PAYLOAD_PREFIX;

        byte[] checksum = Hash.sha256(Hash.sha256(payload));
        byte[] rawAddress = new byte[25];
        System.arraycopy(payload, 0, rawAddress, 0, 21);
        System.arraycopy(checksum, 0, rawAddress, 21, CHECKSUM_LENGTH);
        String address = EncodingUtils.base58Ripple(rawAddress);

        return new BIP44Address(address, addressPathED25519);
    }

    private int[] getAddressPath(int account) {
        int purpose = XRPWallet.PURPOSE | HARDENED;
        int coinCode = XRPWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, 0, 0};
    }

    private int[] getAddressPathED25519(int account) {
        int purpose = XRPWallet.PURPOSE | HARDENED;
        int coinCode = XRPWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
