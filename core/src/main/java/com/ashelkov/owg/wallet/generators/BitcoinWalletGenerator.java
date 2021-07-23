package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import com.ashelkov.owg.address.BIP84Address;
import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.BitcoinWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class BitcoinWalletGenerator extends WalletGenerator {

    private static final String BECH32_HRP = "bc";
    private static final byte WITNESS_VERSION = (byte)0x00;

    private final Bip32ECKeyPair masterKeyPair;

    public BitcoinWalletGenerator(byte[] seed) {
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, BitcoinWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, BitcoinWallet.COIN);
    }

    @Override
    public BitcoinWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

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

        return new BitcoinWallet(addresses);
    }

    @Override
    public BitcoinWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new BitcoinWallet(wrapper);
    }

    private BIP84Address generateAddress(int account, int change, int index) {

        int[] addressPath = getAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        byte[] unencodedAddress = EncodingUtils.to5BitBytesSafe(
                Hash.sha256hash160(
                        derivedKeyPair
                                .getPublicKeyPoint()
                                .getEncoded(true)));
        byte[] unencodedAddressWithWitness = new byte[unencodedAddress.length + 1];
        unencodedAddressWithWitness[0] = WITNESS_VERSION;
        System.arraycopy(unencodedAddress, 0, unencodedAddressWithWitness, 1, unencodedAddress.length);

        String address = Bech32.encode(BECH32_HRP, unencodedAddressWithWitness);

        return new BIP84Address(address, addressPath);
    }

    private int[] getAddressPath(int account, int change, int index) {
        int purpose = BitcoinWallet.PURPOSE | HARDENED;
        int coinCode = BitcoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
