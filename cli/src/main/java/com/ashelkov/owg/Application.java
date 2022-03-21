package com.ashelkov.owg;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.MnemonicUtils;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.io.Params;
import com.ashelkov.owg.io.command.MultiCommand;
import com.ashelkov.owg.io.command.SoloCommand;
import com.ashelkov.owg.io.command.coin.AvalancheCommand;
import com.ashelkov.owg.io.command.coin.MoneroCommand;
import com.ashelkov.owg.io.command.coin.XRPCommand;
import com.ashelkov.owg.util.Utils;
import com.ashelkov.owg.wallet.SingleCoinWallet;
import com.ashelkov.owg.wallet.MultiCoinWallet;
import com.ashelkov.owg.wallet.Wallet;
import com.ashelkov.owg.wallet.generators.*;

/**
 * Main class for OWG CLI.
 */
public final class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Params params = Params.getInstance();

    private Application() {}

    /**
     * Create factory for selected coin using given random seed.
     *
     * @param coin Coin for which to create factory
     * @param seed Random seed
     * @return Factory ready to generate wallet
     */
    private static SingleCoinWalletGenerator getWalletGenerator(Coin coin, byte[] seed) {
        return switch (coin) {
            case BTC ->
                new BitcoinWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case LTC ->
                new LitecoinWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case DOGE ->
                new DogecoinWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case ETH ->
                new EthereumWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case XMR ->
                new MoneroWalletGenerator(
                        seed,
                        MoneroCommand.getInstance().isGenViewKey(),
                        MoneroCommand.getInstance().isGenSpendKey(),
                        params.isGenPrivKey());

            case XRP ->
                new XRPWalletGenerator(
                        seed,
                        XRPCommand.getInstance().isLegacy(),
                        params.isGenPrivKey(),
                        params.isGenPubKey());

            case XLM ->
                new StellarWalletGenerator(seed, params.isGenPrivKey());

            case ALGO ->
                new AlgorandWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case ERG ->
                new ErgoWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case HNS ->
                new HandshakeWalletGenerator(seed, params.isGenPrivKey(), params.isGenPubKey());

            case AVAX ->
                new AvalancheWalletGenerator(
                        seed,
                        AvalancheCommand.getInstance().getChains(),
                        params.isGenPrivKey(),
                        params.isGenPubKey());
        };
    }

    /**
     * Generate a wallet containing the default address for every coin supported by the OWG.
     *
     * @param seed Random seed
     * @return New wallet
     */
    private static MultiCoinWallet generateMultiWallet(byte[] seed) {

        List<SingleCoinWallet> subWallets = new ArrayList<>(Coin.values().length);

        for (Coin coin : Coin.values()) {
            try {
                SingleCoinWalletGenerator singleCoinWalletGenerator = getWalletGenerator(coin, seed);
                subWallets.add(singleCoinWalletGenerator.generateDefaultWallet());
            } catch (Exception e) {
                System.exit(1);
            }
        }

        return new MultiCoinWallet(subWallets);
    }

    /**
     * Application entry point.
     *
     * @param args CLI arguments (commands and options)
     */
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

        Wallet wallet = null;

        switch (params.getCommand()) {
            case SoloCommand.NAME -> {
                try {
                    SingleCoinWalletGenerator singleCoinWalletGenerator = getWalletGenerator(params.getCoin(), seedFromMnemonic);
                    wallet = singleCoinWalletGenerator.generatePathWallet(params.getBipPath(), params.getNumAddresses());
                } catch (Exception e) {
                    System.exit(1);
                }
            }

            case MultiCommand.NAME ->
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
