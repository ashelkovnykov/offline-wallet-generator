package com.ashelkov.wallet.bip.wallet;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.address.DogecoinAddress;
import com.ashelkov.wallet.bip.util.Bip44Utils;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class DogecoinWallet extends Wallet {

    private static final int P2PKH_VERSION = 30;

    private final Bip32ECKeyPair masterKeyPair;

    public DogecoinWallet(byte[] seed) {

        super(Coin.DOGE);

        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    public List<Bip44Address> generateAddresses(Integer account, Integer change, Integer index, int numAddresses) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = 0;
        }
        if (change == null) {
            logMissing(CHANGE);
            change = 0;
        }
        if (index == null) {
            logMissing(INDEX);
            index = 0;
        }

        List<Bip44Address> result = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(account, change, i));
        }

        return result;
    }

    @Override
    public List<Bip44Address> generateDefaultAddresses(int numAddresses) {

        List<Bip44Address> result = new ArrayList<>(numAddresses);
        int account = 0;
        int change = 0;
        int index = 0;

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(account, change, i));
        }

        return result;
    }

    private Bip44Address getAddress(int account, int change, int index) {

        int[] derivedKeyPath = getDerivedKeyPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivedKeyPath);

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
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new DogecoinAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int account, int change, int index) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
