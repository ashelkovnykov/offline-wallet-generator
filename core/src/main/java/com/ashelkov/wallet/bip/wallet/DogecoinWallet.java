package com.ashelkov.wallet.bip.wallet;

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
    public Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex) {

        if (account == null) {
            logMissing(ACCOUNT);
            account = 0;
        }
        if (change == null) {
            logMissing(CHANGE);
            change = 0;
        }
        if (addressIndex == null) {
            logMissing(INDEX);
            addressIndex = 0;
        }

        return getAddress(account, change, addressIndex);
    }

    @Override
    public Bip44Address getDefaultAddress(int index) {
        return getAddress(0, 0, index);
    }

    private Bip44Address getAddress(int account, int change, int addressIndex) {

        int[] derivedKeyPath = getDerivedKeyPath(account, change, addressIndex);
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

    private int[] getDerivedKeyPath(int account, int change, int addressIndex) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, addressIndex};
    }
}
