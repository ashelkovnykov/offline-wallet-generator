package com.ashelkov.wallet.bip.wallet;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Keys;

import com.ashelkov.wallet.bip.util.Bip44Utils;
import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.address.EthereumAddress;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class EthereumWallet extends Wallet {

    private final Bip32ECKeyPair masterKeyPair;

    public EthereumWallet(byte[] seed) {

        super(Coin.ETH);

        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    public Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex) {

        if (account != null) {
            logWarning(ACCOUNT, coinName, account);
        }
        if (change != null) {
            logWarning(CHANGE, coinName, change);
        }
        if (addressIndex == null) {
            logMissing(INDEX, coinName);
            addressIndex = 0;
        }

        return getAddress(addressIndex);
    }

    @Override
    public Bip44Address getDefaultAddress(int index) {
        return getAddress(index);
    }

    private Bip44Address getAddress(int addressIndex) {

        int[] derivedKeyPath = getDerivedKeyPath(addressIndex);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivedKeyPath);

        String address = Keys.toChecksumAddress(Keys.getAddress(derivedKeyPair));
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new EthereumAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int addressIndex) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;
        int account = HARDENED; // 0 | 0x80000000
        int change = 0;

        return new int[] {purpose, coinCode, account, change, addressIndex};
    }
}
