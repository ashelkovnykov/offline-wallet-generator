package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.ErgoWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.ERG;
import static com.ashelkov.owg.bip.Constants.CHECKSUM_LENGTH;
import static com.ashelkov.owg.bip.Constants.HARDENED;

/**
 * Factory class to generate [[ErgoWallet]] objects.
 */
public class ErgoWalletGenerator extends IndexWalletGenerator {

    // First 4 bits:
    // 0x00 = Mainnet
    // 0x10 = Testnet
    //
    // Second 4 bits:
    // 0x01 = Pay-to-Public-Key (P2PK)
    // 0x02 = Pay-to-Script-Hash (P2SH)
    // 0x03 = Pay-to-Script (P2S)
    private static final byte PREFIX_BYTE = (byte)0x01;

    private final Bip32ECKeyPair masterKeyPair;

    public ErgoWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    /**
     * Generate the default [[ErgoWallet]] ('index' field has default value).
     *
     * @return New Ergo wallet containing only the address m/44'/429'/0'/0/0
     */
    @Override
    public ErgoWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new ErgoWallet(wrapper);
    }

    /**
     * Generate a [[ErgoWallet]] for a particular BIP-44 'index'. Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New Ergo wallet
     */
    @Override
    public ErgoWallet generateWallet(int index, int numAddresses) {

        List<BIP44Address> addresses = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            addresses.add(generateAddress(i));
        }

        return new ErgoWallet(addresses);
    }

    /**
     * Generate the Ergo address for a particular BIP-44 'index'.
     *
     * @param index Index value
     * @return Ergo address
     */
    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        byte[] addressPreChecksum = new byte[34];
        byte[] addressWithChecksum = new byte[38];

        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        addressPreChecksum[0] = PREFIX_BYTE;
        System.arraycopy(
            derivedKeyPair.getPublicKeyPoint().getEncoded(true),
            0,
            addressPreChecksum,
            1,
            33);
        System.arraycopy(
            addressPreChecksum,
            0,
            addressWithChecksum,
            0,
            34);
        System.arraycopy(
            Hash.blake2b256(addressPreChecksum),
            0,
            addressWithChecksum,
            34,
            CHECKSUM_LENGTH);

        String address = EncodingUtils.base58Bitcoin(addressWithChecksum);
        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPrivateKeyBytes33()).substring(2);
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        }

        return new BIP44Address(address, addressPath, privKeyText, pubKeyText);
    }

    /**
     * Generate the full BIP-44 path for a given Ergo index value.
     *
     * @param index Index value
     * @return BIP-44 path for the given index
     */
    private int[] getAddressPath(int index) {
        int purpose = BIP44Address.PURPOSE | HARDENED;
        int coinCode = ERG.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
