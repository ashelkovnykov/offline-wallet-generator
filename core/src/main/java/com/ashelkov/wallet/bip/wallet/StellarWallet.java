package com.ashelkov.wallet.bip.wallet;

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
    public Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex) {

        if (account == null) {
            logMissing(ACCOUNT, coinName);
            account = 0;
        }
        if (change != null) {
            logWarning(CHANGE, coinName, change);
        }
        if (addressIndex != null) {
            logWarning(INDEX, coinName, addressIndex);
        }

        return getAddress(account);
    }

    @Override
    public Bip44Address getDefaultAddress(int index) {
        return getAddress(index);
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
