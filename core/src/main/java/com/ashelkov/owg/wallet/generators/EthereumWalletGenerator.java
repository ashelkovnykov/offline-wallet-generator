package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Keys;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.EthereumWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.ETH;
import static com.ashelkov.owg.bip.Constants.HARDENED;

public class EthereumWalletGenerator extends IndexWalletGenerator {

    private final Bip32ECKeyPair masterKeyPair;

    public EthereumWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    public EthereumWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new EthereumWallet(wrapper);
    }

    @Override
    public EthereumWallet generateWallet(int index, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new EthereumWallet(addresses);
    }

    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        String address = Keys.toChecksumAddress(Keys.getAddress(derivedKeyPair));

        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = String.format(
                "0x%s",
                EncodingUtils.bytesToHex(derivedKeyPair.getPrivateKeyBytes33()).substring(2));
        }
        if (genPubKey) {
            pubKeyText = String.format(
                "0x%s",
                EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true)));
        }

        return new BIP44Address(address, addressPath, privKeyText, pubKeyText);
    }

    private int[] getAddressPath(int index) {
        int purpose = EthereumWallet.PURPOSE | HARDENED;
        int coinCode = ETH.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
