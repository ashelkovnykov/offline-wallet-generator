package com.ashelkov.wallet.bip.wallet;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Bech32;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Hash;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.AvalancheAddress;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.util.EncodingUtils;
import com.ashelkov.wallet.bip.util.Bip44Utils;

import static com.ashelkov.wallet.bip.Constants.HARDENED;
import static com.ashelkov.wallet.bip.Constants.PURPOSE_44;

public class AvalancheWallet extends Wallet {

    // 3 Base chains in Avalanche: Exchange (X), Platform (P), and Contracts (C)
    private static final String CHAIN_CODE = "X";
    private static final String CHAIN_DELIMITER = "-";
    private static final String BECH32_HRP = "avax";

    // [chain code] + [chain delimiter] + [hrp] + "1" + [32 byte address] + [6 byte checksum] = 45
    private static final int ADDRESS_LENGTH = 45;

    private final Bip32ECKeyPair masterKeyPair;

    public AvalancheWallet(byte[] seed) {

        super(Coin.AVAX);

        this.masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Override
    public List<Bip44Address> generateAddresses(Integer account, Integer change, Integer index, int numAddresses) {

        if (account != null) {
            logWarning(ACCOUNT, account);
        }
        if (change != null) {
            logWarning(CHANGE, change);
        }
        if (index == null) {
            logMissing(INDEX);
            index = 0;
        }

        List<Bip44Address> result = new ArrayList<>(numAddresses);

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    @Override
    public List<Bip44Address> generateDefaultAddresses(int numAddresses) {

        List<Bip44Address> result = new ArrayList<>(numAddresses);
        int index = 0;

        for(int i = index; i < (index + numAddresses); ++i) {
            result.add(getAddress(i));
        }

        return result;
    }

    /**
     *
     * https://support.avalabs.org/en/articles/4596397-what-is-an-address
     * https://support.avalabs.org/en/articles/4587392-what-is-bech32
     *
     * @param index
     * @return
     */
    private Bip44Address getAddress(int index) {

        int[] derivedKeyPath = getDerivedKeyPath(index);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivedKeyPath);

        StringBuilder addressBuilder = new StringBuilder(ADDRESS_LENGTH);
        addressBuilder.append(CHAIN_CODE);
        addressBuilder.append(CHAIN_DELIMITER);
        addressBuilder.append(
                Bech32.encode(
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
        String bip44Path = Bip44Utils.convertPathToText(derivedKeyPath);

        return new AvalancheAddress(address, bip44Path);
    }

    private int[] getDerivedKeyPath(int index) {

        int purpose = PURPOSE_44 | HARDENED;
        int coinCode = coin.getCode() | HARDENED;
        int account = HARDENED; // 0 | 0x80000000
        int change = 0;

        return new int[] {purpose, coinCode, account, change, index};
    }
}
