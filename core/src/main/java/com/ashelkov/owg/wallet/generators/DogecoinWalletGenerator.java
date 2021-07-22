package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.DogecoinWallet;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class DogecoinWalletGenerator extends WalletGenerator {

    private static final int P2PKH_VERSION = 30;

    private final Bip32ECKeyPair masterKeyPair;

    public DogecoinWalletGenerator(byte[] seed) {
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, DogecoinWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, DogecoinWallet.COIN);
    }

    @Override
    public DogecoinWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

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

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(account, change, i));
        }

        return new DogecoinWallet(addresses);
    }

    @Override
    public DogecoinWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new DogecoinWallet(wrapper);
    }

    private BIP44Address generateAddress(int account, int change, int index) {

        int[] addressPath = getAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        String address = Base58.encodeChecked(
                P2PKH_VERSION,
                LegacyAddress
                    .fromPubKeyHash(
                            MainNetParams.get(),
                            Hash.sha256hash160(
                                    derivedKeyPair
                                            .getPublicKeyPoint()
                                            .getEncoded(true)))
                    .getHash());

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int account, int change, int index) {
        int purpose = DogecoinWallet.PURPOSE | HARDENED;
        int coinCode = DogecoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
