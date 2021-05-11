package com.ashelkov.wallet;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.wallet.Wallet;
import com.ashelkov.wallet.bip.wallet.WalletFactory;
import com.ashelkov.wallet.io.FileUtils;
import com.ashelkov.wallet.io.Parameters;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Parameters params = Parameters.getInstance();
    private static final JCommander commander = JCommander.newBuilder().addObject(params).build();
    private static final SecureRandom rng = Utils.getSecureRandomInstance();

    private enum OutputMode {
        MNEMONIC_ONLY, DEFAULT_ADDRESS, SPECIFIC_ADDRESS, HOT_WALLET
    }

    public static void main(String[] args) {

        //
        // Parse input parameters
        //

        try {
            commander.parse(args);
        } catch (ParameterException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        if (params.isHelp()) {
            commander.usage();
            System.exit(0);
        }

        OutputMode outputMode = validateInput();

        //
        // Generate mnemonic
        //

        String mnemonic;
        if (params.getCustomMnemonic() != null) {
            mnemonic = params.getCustomMnemonic();

        } else {
            byte[] randomSeed = new byte[params.getEntropy() / 8];
            rng.nextBytes(randomSeed);
            mnemonic = MnemonicUtils.generateMnemonic(randomSeed);
        }

        byte[] seedFromMnemonic = MnemonicUtils.generateSeed(mnemonic, params.getMnemonicPassword());

        // Logging
        logger.trace(mnemonic);

        //
        // Generate wallet/addresses
        //

        List<Bip44Address> addresses;
        int numAddresses = params.getNumAddresses();

        switch (outputMode) {
            case DEFAULT_ADDRESS -> {
                addresses = new ArrayList<>(numAddresses);
                generateDefaultAddresses(addresses, params.getCoin(), seedFromMnemonic, numAddresses);
            }
            case SPECIFIC_ADDRESS -> {
                addresses = new ArrayList<>(1);
                generateSpecificAddress(addresses, seedFromMnemonic);
            }
            case HOT_WALLET -> {
                addresses = new ArrayList<>(numAddresses * Coin.values().length);
                generateHotWallet(addresses, seedFromMnemonic, numAddresses);

            }
            case MNEMONIC_ONLY -> {
                addresses = new ArrayList<>(0);
            }
            default -> {
                // This case can never occur, but the Java compiler cannot detect that this is so
                addresses = new ArrayList<>(0);
            }
        }

        //
        // Output wallet/addresses to file
        //

        logger.info(String.format("Attempting to save wallet to file '%s'", params.getOutputDirectory()));

        FileUtils.saveAddressesToFile(params.getOutputDirectory(), mnemonic, addresses);
    }

    private static OutputMode validateInput() {

        OutputMode result;

        boolean aci = ((params.getAccount() != null) || (params.getChange() != null) || (params.getIndex() != null));
        boolean coin = params.getCoin() != null;
        boolean hot = params.isHot();
        boolean multi = params.getNumAddresses() > 1;

        boolean error = false;

        //
        // Errors
        //

        if (aci && !coin) {
            logger.error(
                    "Setting any of the BIP44 account, change, or index elements explicitly requires specifying a " +
                            "specific coin for which to generate an address");
            error = true;

        } else if (coin && hot) {
            logger.error("Both address mode and hot wallet mode enabled; disable one and re-run");
            error = true;
        }

        if (error) {
            System.exit(1);
        }

        //
        // Warnings
        //

        if (!coin && !hot) {
            logger.warn("Neither address mode nor hot wallet mode enabled; generating mnemonic only");

            if (multi) {
                logger.warn("No addresses to generate; ignoring multi address input");
            }
        }

        if (aci && multi) {
            logger.warn("Creating specific BIP44 address overrides creation of multiple addresses; ignoring multi " +
                    "address input");
        }

        //
        // Determine output mode
        //

        if (aci) {
            result = OutputMode.SPECIFIC_ADDRESS;
        } else if (coin) {
            result = OutputMode.DEFAULT_ADDRESS;
        } else if (hot) {
            result = OutputMode.HOT_WALLET;
        } else {
            result = OutputMode.MNEMONIC_ONLY;
        }

        return result;
    }

    private static void generateDefaultAddresses(List<Bip44Address> addresses, Coin coin, byte[] seed, int iter) {

        Wallet wallet = WalletFactory.generateWallet(seed, coin);

        for (int i = 0; i < iter; ++i) {

            Bip44Address address = wallet.getDefaultAddress(i);
            addresses.add(address);

            logger.trace(address.getCoin());
            logger.trace(address.getPath());
            logger.trace(address.getAddress());
        }
    }

    private static void generateSpecificAddress(List<Bip44Address> addresses, byte[] seed) {

        Wallet wallet = WalletFactory.generateWallet(seed, params.getCoin());
        Bip44Address address = wallet.getSpecificAddress(params.getAccount(), params.getChange(), params.getIndex());
        addresses.add(address);

        logger.trace(address.getCoin());
        logger.trace(address.getPath());
        logger.trace(address.getAddress());
    }

    private static void generateHotWallet(List<Bip44Address> addresses, byte[] seed, int iter) {

        for (Coin coin : Coin.values()) {
            generateDefaultAddresses(addresses, coin, seed, iter);
        }
    }
}
