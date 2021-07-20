package com.ashelkov.wallet.bip.wallet;

import java.util.ArrayList;
import java.util.List;

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
    public List<Bip44Address> generateAddresses(Integer account, Integer change, Integer index, int numAddresses) {

        if (account != null) {
            logWarning(ACCOUNT, account);
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index == null) {
            logMissing(INDEX);
            index = 0;
        }

        List<Bip44Address> result = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    @Override
    public List<Bip44Address> generateDefaultAddresses(int numAddresses) {

        List<Bip44Address> result = new ArrayList<>(numAddresses);
        int index = 0;

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    private Bip44Address getAddress(int index) {

        int[] derivedKeyPath = getDerivedKeyPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivedKeyPath);

        String address = Keys.toChecksumAddress(Keys.getAddress(derivedKeyPair));
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new EthereumAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int index) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;
        int account = HARDENED; // 0 | 0x80000000
        int change = 0;

        return new int[] {purpose, coinCode, account, change, index};
    }
}
