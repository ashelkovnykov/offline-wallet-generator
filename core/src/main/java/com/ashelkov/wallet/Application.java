package com.ashelkov.wallet;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;
import com.ashelkov.wallet.bip.wallet.Wallet;
import com.ashelkov.wallet.bip.wallet.WalletFactory;
import com.ashelkov.wallet.io.util.FileUtils;
import com.ashelkov.wallet.io.Params;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Params params = Params.getInstance();
    private static final SecureRandom rng = Utils.getSecureRandomInstance();

    public static void main(String[] args) {

        //
        // Parse input parameters
        //

        try {
            params.parseArguments(args);
        } catch (ParameterException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        if (params.isHelp()) {
            params.printUsage();
            System.exit(0);
        }

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

        // Logging
        logger.trace(mnemonic);

        //
        // Generate seed
        //

        byte[] seedFromMnemonic = MnemonicUtils.generateSeed(mnemonic, params.getMnemonicPassword());

        //
        // Generate wallet/addresses
        //

        List<Bip44Address> addresses;

        switch (params.getCommand()) {
            case Params.COMMAND_COLD -> {

                Wallet wallet = WalletFactory.generateWallet(seedFromMnemonic, params.getCoin());
                addresses = wallet.generateAddresses(
                    params.getAccount(),
                    params.getChange(),
                    params.getIndex(),
                    params.getNumAddresses());
            }
            case Params.COMMAND_HOT -> {
                addresses = new ArrayList<>(Coin.values().length);
                generateHotAddresses(addresses, seedFromMnemonic);
            }
            default ->
                throw new IllegalArgumentException("Unrecognized command");
        }

        //
        // Output wallet/addresses to file
        //

        logger.info(String.format("Attempting to save wallet to file '%s'", params.getOutputDirectory()));

        FileUtils.saveAddressesToFile(params.getOutputDirectory(), mnemonic, addresses);
    }

    private static void generateHotAddresses(List<Bip44Address> addresses, byte[] seed) {
        for (Coin coin : Coin.values()) {
            Wallet wallet = WalletFactory.generateWallet(seed, coin);
            addresses.add(wallet.generateDefaultAddresses(1).get(0));
        }
    }
}
