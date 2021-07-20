package com.ashelkov.wallet.bip.wallet;

import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;

import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.address.StellarAddress;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class StellarWallet extends Wallet {

    private final byte[] seed;

    public StellarWallet(byte[] seed) {

        super(Coin.XLM);

        this.seed = seed;
    }

    @Override
    public List<Bip44Address> generateAddresses(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = 0;
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index != null) {
            logWarning(INDEX, index);
        }

        List<Bip44Address> result = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    @Override
    public List<Bip44Address> generateDefaultAddresses(int numAddresses) {

        List<Bip44Address> result = new ArrayList<>(numAddresses);
        int account = 0;

        for(int i = account; i < (account + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    private Bip44Address getAddress(int account) {

        int[] derivedKeyPath = getDerivedKeyPath(account);
        KeyPair derivedKeyPair = KeyPair.fromBip39Seed(seed, account);

        String address = derivedKeyPair.getAccountId();
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new StellarAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int account) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
