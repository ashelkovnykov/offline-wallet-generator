package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Keys;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.EthereumWallet;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class EthereumWalletGenerator extends WalletGenerator {

    private final Bip32ECKeyPair masterKeyPair;

    public EthereumWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, EthereumWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, EthereumWallet.COIN);
    }

    @Override
    public EthereumWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

        if (account != null) {
            logWarning(ACCOUNT, account);
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index == null) {
            logMissing(INDEX);
            index = DEFAULT_FIELD_VAL;
        }

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new EthereumWallet(addresses);
    }

    @Override
    public EthereumWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new EthereumWallet(wrapper);
    }

    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        String address = Keys.toChecksumAddress(Keys.getAddress(derivedKeyPair));

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int index) {
        int purpose = EthereumWallet.PURPOSE | HARDENED;
        int coinCode = EthereumWallet.COIN.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
