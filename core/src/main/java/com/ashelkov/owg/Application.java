package com.ashelkov.owg;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.io.Params;
import com.ashelkov.owg.util.Utils;
import com.ashelkov.owg.wallet.ColdWallet;
import com.ashelkov.owg.wallet.HotWallet;
import com.ashelkov.owg.wallet.Wallet;
import com.ashelkov.owg.wallet.generators.WalletGenerator;
import com.ashelkov.owg.wallet.generators.WalletGeneratorFactory;

public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Params params = Params.getInstance();

    private Application() {}

    private static HotWallet generateHotWallet(byte[] seed) {

        List<ColdWallet> subwallets = new ArrayList<>(Coin.values().length);

        for (Coin coin : Coin.values()) {
            WalletGenerator walletGenerator = WalletGeneratorFactory.getGenerator(seed, coin);
            subwallets.add(walletGenerator.generateDefaultWallet());
        }

        return new HotWallet(subwallets);
    }

    public static void main(String[] args) {

        //
        // Setup
        //

        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(
                    ClassLoader.getSystemClassLoader().getResourceAsStream("logging.properties"));
        } catch(Exception e) {
            System.err.println("Error loading logging properties; proceeding without logging");
        }

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
            Utils.getSecureRandom().nextBytes(randomSeed);
            mnemonic = MnemonicUtils.generateMnemonic(randomSeed);
        }

        //
        // Generate seed
        //

        byte[] seedFromMnemonic = MnemonicUtils.generateSeed(mnemonic, params.getMnemonicPassword());

        //
        // Generate wallet/addresses
        //

        Wallet wallet;

        switch (params.getCommand()) {
            case Params.COMMAND_COLD -> {

                    WalletGenerator walletGenerator = WalletGeneratorFactory.getGenerator(seedFromMnemonic, params.getCoin());
                    wallet = walletGenerator.generateWallet(
                        params.getAccount(),
                        params.getChange(),
                        params.getIndex(),
                        params.getNumAddresses());
                }

            case Params.COMMAND_HOT ->
                wallet = generateHotWallet(seedFromMnemonic);

            default ->
                throw new IllegalArgumentException("Unrecognized command");
        }

        //
        // Output wallet/addresses
        //

        params.getOutputWriter().saveWallet(mnemonic, wallet);
    }
}
