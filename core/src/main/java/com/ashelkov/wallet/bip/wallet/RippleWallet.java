package com.ashelkov.wallet.bip.wallet;

import org.stellar.sdk.KeyPair;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.wallet.bip.util.EncodingUtils;
import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.address.RippleAddress;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class RippleWallet extends Wallet {

    private static final byte MASTER_PUB_KEY_PREFIX = (byte)0xED;
    private static final byte PAYLOAD_PREFIX = (byte)0x00;

    private final Bip32ECKeyPair masterKeyPair;
    private final byte[] seed;

    public RippleWallet(byte[] seed) {

        super(Coin.XRP);

        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
        this.seed = seed;
    }

    @Override
    public Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = 0;
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (addressIndex != null) {
            logWarning(INDEX, addressIndex);
        }

        return getAddress(account);
    }

    @Override
    public Bip44Address getDefaultAddress(int index) {
        return getAddress(index);
    }

    /**
     *
     * https://xrpl.org/accounts.html#address-encoding
     *
     * @param account
     * @return
     */
    private Bip44Address getAddress(int account) {

        int[] derivedKeyPath = getDerivedKeyPath(account);
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivedKeyPath);

        byte[] accountID = Hash.sha256hash160(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        byte[] payload = new byte[21];
        System.arraycopy(accountID, 0, payload, 1, 20);
        payload[0] = PAYLOAD_PREFIX;

        byte[] checksum = Hash.sha256(Hash.sha256(payload));
        byte[] rawAddress = new byte[25];
        System.arraycopy(payload, 0, rawAddress, 0, 21);
        System.arraycopy(checksum, 0, rawAddress, 21, 4);
        String address = EncodingUtils.base58Ripple(rawAddress);

        return new RippleAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int account) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;
        int change = 0;
        int addressIndex = 0;

        return new int[] {purpose, coinCode, account | HARDENED, change, addressIndex};
    }

    private Bip44Address getAddressED25519(int account) {

        int[] derivedKeyPath = getDerivedKeyPath(account);
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        byte[] rawPublicKey = KeyPair
                .fromSecretSeed(Bip44Utils.deriveEd25519PrivateKey(seed, derivedKeyPath))
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
        System.arraycopy(checksum, 0, rawAddress, 21, 4);
        String address = EncodingUtils.base58Ripple(rawAddress);

        return new RippleAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPathED25519(int account) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
