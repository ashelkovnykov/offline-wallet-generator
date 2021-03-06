package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.DogecoinWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.DOGE;
import static com.ashelkov.owg.bip.Constants.HARDENED;

/**
 * Factory class to generate [[DogecoinWallet]] objects.
 */
public class DogecoinWalletGenerator extends ACIWalletGenerator {

    private static final byte DOGE_IDENTIFICATION_PREFIX = (byte)0x9E;
    private static final int P2PKH_VERSION = 30;

    private final Bip32ECKeyPair masterKeyPair;

    public DogecoinWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    /**
     * Generate the default [[DogecoinWallet]] ('account', 'change', 'index', etc. fields have default values).
     *
     * @return New Dogecoin wallet containing only the address m/44'/3'/0'/0/0
     */
    @Override
    public DogecoinWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new DogecoinWallet(wrapper);
    }

    /**
     * Generate a [[DogecoinWallet]] for a particular BIP-44 path. Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New Dogecoin wallet
     */
    @Override
    public DogecoinWallet generateWallet(int account, int change, int index, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(account, change, i));
        }

        return new DogecoinWallet(addresses);
    }

    /**
     * Generate the Dogecoin address for a particular BIP-44 path.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @return Dogecoin address
     */
    private BIP44Address generateAddress(int account, int change, int index) {

        int[] addressPath = getAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

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

        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = BitcoinWalletGenerator.generatePrivateKey(
                derivedKeyPair.getPrivateKeyBytes33(),
                DOGE_IDENTIFICATION_PREFIX);
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        }

        return new BIP44Address(address, addressPath, privKeyText, pubKeyText);
    }

    /**
     * Generate the full BIP-44 path for given Dogecoin account, change, and index values.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @return BIP-44 path for the given values
     */
    private int[] getAddressPath(int account, int change, int index) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = DOGE.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
