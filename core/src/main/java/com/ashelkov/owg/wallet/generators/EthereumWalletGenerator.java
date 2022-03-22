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

/**
 * Factory class to generate [[EthereumWallet]] objects.
 */
public class EthereumWalletGenerator extends IndexWalletGenerator {

    private final Bip32ECKeyPair masterKeyPair;

    public EthereumWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    /**
     * Generate the default [[EthereumWallet]] ('index' field has default value).
     *
     * @return New Ethereum wallet containing only the address m/44'/44'/0'/0/0
     */
    @Override
    public EthereumWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new EthereumWallet(wrapper);
    }

    /**
     * Generate a [[EthereumWallet]] for a particular BIP-44 'index'. Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New Ethereum wallet
     */
    @Override
    public EthereumWallet generateWallet(int index, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new EthereumWallet(addresses);
    }

    /**
     * Generate the Ethereum address for a particular BIP-44 'index'.
     *
     * @param index Index value
     * @return Ethereum address
     */
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

    /**
     * Generate the full BIP-44 path for a given Ethereum index value.
     *
     * @param index Index value
     * @return BIP-44 path for the given index
     */
    private int[] getAddressPath(int index) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = ETH.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
