package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.StellarWallet;

import static com.ashelkov.owg.bip.Coin.XLM;
import static com.ashelkov.owg.bip.Constants.HARDENED;

/**
 * Factory class to generate [[StellarWallet]] objects.
 */
public class StellarWalletGenerator extends AccountWalletGenerator {

    public StellarWalletGenerator(byte[] seed, boolean genPrivKey) {
        super(seed, genPrivKey, false);
    }

    /**
     * Generate the default [[StellarWallet]] ('account' field has default value).
     *
     * @return New Stellar wallet containing only the address m/44'/148'/0'
     */
    @Override
    public StellarWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new StellarWallet(wrapper);
    }

    /**
     * Generate a [[StellarWallet]] for a particular BIP-44 'account'. Optionally, generate more than one address by
     * incrementing the 'account' field.
     *
     * @param account Account value
     * @param numAddresses Number of addresses to generate
     * @return New Stellar wallet
     */
    @Override
    public StellarWallet generateWallet(int account, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = account; i < (account + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new StellarWallet(addresses);
    }

    /**
     * Generate the Stellar address for a particular BIP-44 'account'.
     *
     * @param account Account value
     * @return Stellar address
     */
    private BIP44Address generateAddress(int account) {

        int[] addressPath = getAddressPath(account);
        KeyPair derivedKeyPair = KeyPair.fromBip39Seed(seed, account);

        String address = derivedKeyPair.getAccountId();

        String privKeyText = null;
        if (genPrivKey) {
            privKeyText = String.valueOf(derivedKeyPair.getSecretSeed());
        }

        return new BIP44Address(address, addressPath, privKeyText, null);
    }

    /**
     * Generate the full BIP-44 path for a given Stellar account value.
     *
     * @param account Account value
     * @return BIP-44 path for the given account
     */
    private int[] getAddressPath(int account) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = XLM.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }
}
