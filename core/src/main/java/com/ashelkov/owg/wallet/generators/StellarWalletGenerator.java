package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.StellarWallet;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class StellarWalletGenerator extends AccountWalletGenerator {

    private final byte[] seed;

    public StellarWalletGenerator(byte[] seed, boolean genPrivKey) {
        super(genPrivKey, false);
        this.seed = seed;
    }

    @Override
    public StellarWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new StellarWallet(wrapper);
    }

    @Override
    public StellarWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new StellarWallet(addresses);
    }

    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);
        KeyPair derivedKeyPair = KeyPair.fromBip39Seed(seed, account);

        String address = derivedKeyPair.getAccountId();

        String privKeyText = null;
        if (genPrivKey) {
            privKeyText = String.valueOf(derivedKeyPair.getSecretSeed());
        }

        return new BIP44Address(address, addressPath, privKeyText, null);
    }

    private int[] getAddressPath(int account) {
        int purpose = StellarWallet.PURPOSE | HARDENED;
        int coinCode = StellarWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
