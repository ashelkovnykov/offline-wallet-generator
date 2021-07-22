package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.StellarWallet;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class StellarWalletGenerator extends WalletGenerator {

    private final byte[] seed;

    public StellarWalletGenerator(byte[] seed) {
        this.seed = seed;
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, StellarWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, StellarWallet.COIN);
    }

    @Override
    public StellarWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

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

        return new StellarWallet(addresses);
    }

    @Override
    public StellarWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new StellarWallet(wrapper);
    }

    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);
        KeyPair derivedKeyPair = KeyPair.fromBip39Seed(seed, account);

        String address = derivedKeyPair.getAccountId();

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int account) {
        int purpose = StellarWallet.PURPOSE | HARDENED;
        int coinCode = StellarWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
