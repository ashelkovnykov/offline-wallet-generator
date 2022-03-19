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

import static com.ashelkov.owg.bip.Coin.XRP;
import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;

public class XRPWalletGenerator extends AccountWalletGenerator {

    private static final byte MASTER_PUB_KEY_PREFIX = (byte)0xED;
    private static final byte PAYLOAD_PREFIX = (byte)0x00;

    private final Bip32ECKeyPair masterKeyPair;
    private final boolean legacy;

    public XRPWalletGenerator(byte[] seed, boolean legacy, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
        this.legacy = legacy;
    }

    @Override
    public XRPWallet generateDefaultWallet() {
        return generateWallet(DEFAULT_FIELD_VAL, 1);
    }

    @Override
    public XRPWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {

            BIP44Address address;
            if (legacy) {
                address = generateAddressSECP256k1(i);
            } else {
                address = generateAddressED25519(i);
            }

            addresses.add(address);
        }

        return new XRPWallet(addresses);
    }

    /**
     *
     * https://xrpl.org/accounts.html#address-encoding
     *
     * @param account
     * @return
     */
    private BIP44Address generateAddressSECP256k1(int account) {

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
        int coinCode = XRP.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, 0, 0};
    }

    private int[] getAddressPathED25519(int account) {
        int purpose = XRPWallet.PURPOSE | HARDENED;
        int coinCode = XRP.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
