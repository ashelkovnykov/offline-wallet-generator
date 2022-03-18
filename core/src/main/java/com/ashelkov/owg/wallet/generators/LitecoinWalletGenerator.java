package com.ashelkov.owg.wallet.generators;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.wallet.LitecoinWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class LitecoinWalletGenerator extends ACIWalletGenerator {

    private static final String BECH32_HRP = "ltc";
    private static final byte WITNESS_VERSION = (byte)0x00;
    private static final byte LTC_IDENTIFICATION_PREFIX = (byte)0xB0;
    private static final int XPUB_VERSION = 0x04b24746;
    // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Serialization_format
    private static final int XPUB_CORE_LENGTH = 78;
    private static final int XPUB_CHECKSUM_LENGTH = 4;

    private final Bip32ECKeyPair masterKeyPair;

    public LitecoinWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    public LitecoinWallet generateDefaultWallet() {

        BIP84Address masterPubKey = generateExtendedKey(DEFAULT_FIELD_VAL);
        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateDerivedAddress(DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL, DEFAULT_FIELD_VAL));

        return new LitecoinWallet(masterPubKey, wrapper);
    }

    @Override
    public LitecoinWallet generateWallet(int account, int change, int index, int numAddresses) {

        BIP84Address masterPubKey = generateExtendedKey(account);

        List<BIP44Address> derivedAddresses = new ArrayList<>(numAddresses);
        for(int i = index; i < (index + numAddresses); ++i) {
            derivedAddresses.add(generateDerivedAddress(account, change, i));
        }

        return new LitecoinWallet(masterPubKey, derivedAddresses);
    }

    private BIP84Address generateDerivedAddress(int account, int change, int index) {

        int[] addressPath = getDerivedAddressPath(account, change, index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        byte[] unencodedAddress = EncodingUtils.to5BitBytesSafe(
                        Hash.sha256hash160(
                                derivedKeyPair
                                        .getPublicKeyPoint()
                                        .getEncoded(true)));
        byte[] unencodedAddressWithWitness = new byte[unencodedAddress.length + 1];
        unencodedAddressWithWitness[0] = WITNESS_VERSION;
        System.arraycopy(unencodedAddress, 0, unencodedAddressWithWitness, 1, unencodedAddress.length);

        String address = Bech32.encode(Bech32.Encoding.BECH32, BECH32_HRP, unencodedAddressWithWitness);

        String privKeyText = null;
        String pubKeyText = null;
        if (genPrivKey) {
            privKeyText = BitcoinWalletGenerator.generatePrivateKey(
                    derivedKeyPair.getPrivateKeyBytes33(),
                    LTC_IDENTIFICATION_PREFIX);
        }
        if (genPubKey) {
            pubKeyText = EncodingUtils.bytesToHex(derivedKeyPair.getPublicKeyPoint().getEncoded(true));
        }

        return new BIP84Address(address, addressPath, privKeyText, pubKeyText);
    }

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

    private int[] getAccountAddressPath(int account) {
        int purpose = LitecoinWallet.PURPOSE | HARDENED;
        int coinCode = LitecoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED};
    }

    private int[] getDerivedAddressPath(int account, int change, int index) {
        int purpose = LitecoinWallet.PURPOSE | HARDENED;
        int coinCode = LitecoinWallet.COIN.getCode() | HARDENED;

        return new int[] {purpose, coinCode, account | HARDENED, change, index};
    }
}
