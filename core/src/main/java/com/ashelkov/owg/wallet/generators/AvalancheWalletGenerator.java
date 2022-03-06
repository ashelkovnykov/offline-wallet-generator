package com.ashelkov.owg.wallet.generators;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.wallet.AvalancheWallet;
import com.ashelkov.owg.wallet.util.EncodingUtils;

import static com.ashelkov.owg.bip.Constants.HARDENED;

public class AvalancheWalletGenerator extends WalletGenerator {

    // 3 Base chains in Avalanche: Exchange (X), Platform (P), and Contracts (C)
    private static final String CHAIN_CODE = "X";
    private static final String CHAIN_DELIMITER = "-";
    private static final String BECH32_HRP = "avax";

    // [chain code] + [chain delimiter] + [hrp] + "1" + [32 byte address] + [6 byte checksum] = 45
    private static final int ADDRESS_LENGTH = 45;

    private final Bip32ECKeyPair masterKeyPair;

    public AvalancheWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    protected void logWarning(String field, int val) {
        logWarning(field, AvalancheWallet.COIN, val);
    }

    @Override
    protected void logMissing(String field) {
        logMissing(field, AvalancheWallet.COIN);
    }

    @Override
    public AvalancheWallet generateWallet(Integer account, Integer change, Integer index, int numAddresses) {

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

        return new AvalancheWallet(addresses);
    }

    @Override
    public AvalancheWallet generateDefaultWallet() {

        List<BIP44Address> wrapper = new ArrayList<>(1);
        wrapper.add(generateAddress(DEFAULT_FIELD_VAL));

        return new AvalancheWallet(wrapper);
    }

    /**
     *
     * https://support.avalabs.org/en/articles/4596397-what-is-an-address
     * https://support.avalabs.org/en/articles/4587392-what-is-bech32
     *
     * @param index
     * @return
     */
    private BIP44Address generateAddress(int index) {

        int[] addressPath = getAddressPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, addressPath);

        StringBuilder addressBuilder = new StringBuilder(ADDRESS_LENGTH);
        addressBuilder.append(CHAIN_CODE);
        addressBuilder.append(CHAIN_DELIMITER);
        addressBuilder.append(
                Bech32.encode(
                        Bech32.Encoding.BECH32,
                        BECH32_HRP,
                        EncodingUtils.to5BitBytesSafe(
                                Hash.sha256hash160(
                                        derivedKeyPair
                                                .getPublicKeyPoint()
                                                .getEncoded(true)))));

        // TODO: Move to test
//        byte[] test1 = new byte[] {(byte)0xf8, (byte)0x3e, (byte)0x0f, (byte)0x83, (byte)0xe0};
//        logger.info(Arrays.toString(EncodingUtils.to5BitBytesSafe(test1)));
//        byte[] test2 = new byte[] {(byte)0x07, (byte)0xc1, (byte)0xf0, (byte)0x7c, (byte)0x1f};
//        logger.info(Arrays.toString(EncodingUtils.to5BitBytesSafe(test2)));

        String address = addressBuilder.toString();

        return new BIP44Address(address, addressPath);
    }

    private int[] getAddressPath(int index) {
        int purpose = AvalancheWallet.PURPOSE | HARDENED;
        int coinCode = AvalancheWallet.COIN.getCode() | HARDENED;
        int account = HARDENED; // 0 | HARDENED

        return new int[] {purpose, coinCode, account, 0, index};
    }
}
