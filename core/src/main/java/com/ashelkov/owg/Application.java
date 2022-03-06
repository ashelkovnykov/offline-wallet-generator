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
import com.ashelkov.owg.wallet.SingleCoinWallet;
import com.ashelkov.owg.wallet.MultiCoinWallet;
import com.ashelkov.owg.wallet.Wallet;
import com.ashelkov.owg.wallet.generators.WalletGenerator;
import com.ashelkov.owg.wallet.generators.WalletGeneratorFactory;

public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Params params = Params.getInstance();

    private Application() {}

    private static MultiCoinWallet generateMultiWallet(byte[] seed) {

        List<SingleCoinWallet> subWallets = new ArrayList<>(Coin.values().length);

        for (Coin coin : Coin.values()) {
            WalletGenerator walletGenerator = WalletGeneratorFactory.getGenerator(
                    coin,
                    seed,
                    params.isGenPrivKey(),
                    params.isGenPubKey());
            subWallets.add(walletGenerator.generateDefaultWallet());
        }

        return new MultiCoinWallet(subWallets);
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
            case Params.COMMAND_SOLO -> {

                    WalletGenerator walletGenerator = WalletGeneratorFactory.getGenerator(
                            params.getCoin(),
                            seedFromMnemonic,
                            params.isGenPrivKey(),
                            params.isGenPubKey());
                    wallet = walletGenerator.generateWallet(
                        params.getAccount(),
                        params.getChange(),
                        params.getIndex(),
                        params.getNumAddresses());
                }

            case Params.COMMAND_MULTI ->
                wallet = generateMultiWallet(seedFromMnemonic);

            default ->
                throw new IllegalArgumentException("Unrecognized command");
        }

        //
        // Output wallet/addresses
        //

        params.getOutputWriter().saveWallet(mnemonic, wallet);
    }
}
