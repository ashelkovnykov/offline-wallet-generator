package com.ashelkov.owg.wallet.generators;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.wallet.HandshakeWallet;
import com.ashelkov.owg.wallet.util.DigestUtils;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Coin.HNS;
import static com.ashelkov.owg.bip.Constants.HARDENED;

/**
 * Factory class to generate [[HandshakeWallet]] objects.
 */
public class HandshakeWalletGenerator extends ACIWalletGenerator {

    private static final String BECH32_HRP = "hs";
    private static final byte WITNESS_VERSION = (byte)0x00;
    private static final byte HNS_IDENTIFICATION_PREFIX = (byte)0x80;
    private static final int XPUB_VERSION = 0x0488b21e;
    // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Serialization_format
    private static final int XPUB_CORE_LENGTH = 78;
    private static final int XPUB_CHECKSUM_LENGTH = 4;

    private final Bip32ECKeyPair masterKeyPair;

    public HandshakeWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    /**
     * Generate the default [[HandshakeWallet]] ('account', 'change', 'index', etc. fields have default values).
     *
     * @return New Handshake wallet containing only the address m/84'/5353'/0'/0/0
     */
    @Override
    public HandshakeWallet generateDefaultWallet() {

        BIP84Address masterPubKey = generateExtendedKey(DEFAULT_FIELD_VAL);
        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateDerivedAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new HandshakeWallet(masterPubKey, wrapper);
    }

    /**
     * Generate a [[HandshakeWallet]] for a particular BIP-44 path. Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New Handshake wallet
     */
    @Override
    public HandshakeWallet generateWallet(int account, int change, int index, int numAddresses) {

        BIP84Address masterPubKey = generateExtendedKey(account);

        List<BIP44Address> derivedAddresses = new ArrayList<>(numAddresses);
        for(int i = index; i < (index + numAddresses); ++i) {
            derivedAddresses.add(generateDerivedAddress(account, change, i));
        }

        return new HandshakeWallet(masterPubKey, derivedAddresses);
    }

    /**
     * Generate the Handshake address for a particular BIP-44 path.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @return Handshake address
     */
    private BIP84Address generateDerivedAddress(int account, int change, int index) {

        int[] addressPath = getDerivedAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        byte[] unencodedAddress = EncodingUtils.to5BitBytesSafe(
                DigestUtils.unsafeDigest(
                        DigestUtils.BLAKE2B_160,
                        derivedKeyPair.getPublicKeyPoint().getEncoded(true)));
        byte[] unencodedAddressWithWitness = new byte[unencodedAddress.length + 1];
        unencodedAddressWithWitness[0] = WITNESS_VERSION;
        System.arraycopy(unencodedAddress, 0, unencodedAddressWithWitness, 1, unencodedAddress.length);

        String address = Bech32.encode(Bech32.Encoding.BECH32, BECH32_HRP, unencodedAddressWithWitness);

        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = BitcoinWalletGenerator.generatePrivateKey(
                    derivedKeyPair.getPrivateKeyBytes33(),
                    HNS_IDENTIFICATION_PREFIX);
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        }

        return new BIP84Address(address, addressPath, privKeyText, pubKeyText);
    }

    /**
     * Generate the extended public address for a particular Handshake account.
     *
     * @param account Account value
     * @return Handshake extended public address
     */
    private BIP84Address generateExtendedKey(int account) {

        int[] addressPath = getAccountAddressPath(account);
        Bip32ECKeyPair accountKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        ByteBuffer xpubKeyBuilder = ByteBuffer.allocate(XPUB_CORE_LENGTH);
        xpubKeyBuilder.putInt(XPUB_VERSION);
        xpubKeyBuilder.put((byte)(accountKeyPair.getDepth()));
        xpubKeyBuilder.putInt(accountKeyPair.getParentFingerprint());
        xpubKeyBuilder.putInt(addressPath[2]);
        xpubKeyBuilder.put(accountKeyPair.getChainCode());
        xpubKeyBuilder.put(accountKeyPair.getPublicKeyPoint().getEncoded(true));
        byte[] xpubKeyCore = xpubKeyBuilder.array();
        byte[] checksum = Hash.sha256(Hash.sha256(xpubKeyCore));

        byte[] xpubKey = new byte[XPUB_CORE_LENGTH + XPUB_CHECKSUM_LENGTH];
        System.arraycopy(xpubKeyCore, 0, xpubKey, 0, XPUB_CORE_LENGTH);
        System.arraycopy(checksum, 0, xpubKey, XPUB_CORE_LENGTH, XPUB_CHECKSUM_LENGTH);

        String xpubKeySerialized = EncodingUtils.base58Bitcoin(xpubKey);

        return new BIP84Address(xpubKeySerialized, addressPath);
    }

    /**
     * Generate the full BIP-44 path of a given Handshake account.
     *
     * @param account Account value
     * @return BIP-44 path for the given account
     */
    private int[] getAccountAddressPath(int account) {
        int purpose = BIP84Address.PURPOSE | HARDENED;
        int coinCode = HNS.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }

    /**
     * Generate the full BIP-44 path for given Handshake account, change, and index values.
     *
     * @param account Account value
     * @param change Change value
     * @param index Index value
     * @return BIP-44 path for the given values
     */
    private int[] getDerivedAddressPath(int account, int change, int index) {
        int purpose = BIP84Address.PURPOSE | HARDENED;
        int coinCode = HNS.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
