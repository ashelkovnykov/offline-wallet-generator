package com.ashelkov.wallet.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.PathConverter;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.io.convert.CoinConverter;
import com.ashelkov.wallet.io.validate.*;

/**
 *
 */
final public class Parameters {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_COIN_L = "--coin";
    private static final String OPT_COIN_S = "-c";

    private static final String OPT_ACCOUNT_L = "--account";
    private static final String OPT_ACCOUNT_S = "-a";

    private static final String OPT_CHANGE_L = "--change";
    private static final String OPT_CHANGE_S = "-g";

    private static final String OPT_INDEX_L = "--address-index";
    private static final String OPT_INDEX_S = "-i";

    private static final String OPT_HOT_L = "--hot";
    private static final String OPT_HOT_S = "-t";

    private static final String OPT_OUTPUT_DIR_L = "--target-directory";
    private static final String OPT_OUTPUT_DIR_S = "-d";

    private static final String OPT_ENTROPY_L = "--entropy";
    private static final String OPT_ENTROPY_S = "-e";

    private static final String OPT_MNEMONIC_L = "--custom-mnemonic";
    private static final String OPT_MNEMONIC_S = "-m";

    private static final String OPT_MNEMONIC_PASSWORD_L = "--mnemonic-password";
    private static final String OPT_MNEMONIC_PASSWORD_S = "-p";

    private static final String OPT_NUM_ADDRESSES_L = "--num-addresses";
    private static final String OPT_NUM_ADDRESSES_S = "-n";

    private static final String OPT_HELP_L = "--help";
    private static final String OPT_HELP_S = "-h";

    //
    // CLI Parameter Defaults
    //

    private static final Path DEFAULT_OUTPUT_DIR = Paths.get(FileUtils.getDefaultWalletDir());
    private static final Integer DEFAULT_ENTROPY = 256;

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_COIN_S, OPT_COIN_L},
            description = "Crypto currency code of coin for which to generate wallet",
            converter = CoinConverter.class)
    private Coin coin;

    @Parameter(
            names = {OPT_ACCOUNT_S, OPT_ACCOUNT_L},
            description = "Specific account for which to compute address (see BIP32)",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    private Integer account;

    @Parameter(
            names = {OPT_CHANGE_S, OPT_CHANGE_L},
            description = "Specify whether address is a change address (see BIP32)",
            converter = IntegerConverter.class,
            validateValueWith = BinaryIntegerValidator.class)
    private Integer change;

    @Parameter(
            names = {OPT_INDEX_S, OPT_INDEX_L},
            description = "Specific address index for which to compute address (see BIP32)",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    private Integer index;

    @Parameter(
            names = {OPT_HOT_S, OPT_HOT_L},
            description = "Generate hot wallet for imminent use with a wallet application (will compute addresses " +
                    "for every supported coin type)")
    private boolean hot = false;

    @Parameter(
            names = {OPT_OUTPUT_DIR_S, OPT_OUTPUT_DIR_L},
            description = "Output directory for generated wallet files",
            converter = PathConverter.class)
    private Path outputDirectory = DEFAULT_OUTPUT_DIR;

    @Parameter(
            names = {OPT_ENTROPY_S, OPT_ENTROPY_L},
            description = "Number of bits of entropy for randomly generated seed (must be 128-256 & multiple of 32)",
            converter = IntegerConverter.class,
            validateValueWith = {PositiveIntegerValidator.class, EntropyValidator.class})
    private Integer entropy = DEFAULT_ENTROPY;

    @Parameter(
            names = {OPT_MNEMONIC_S, OPT_MNEMONIC_L},
            description = "Custom mnemonic to use for generating wallets/addresses",
            password = true,
            echoInput = true,
            validateWith = MnemonicValidator.class)
    private String customMnemonic;

    @Parameter(
            names = {OPT_MNEMONIC_PASSWORD_S, OPT_MNEMONIC_PASSWORD_L},
            description = "Password for mnemonic used to generate wallet master key",
            password = true,
            validateWith = PasswordValidator.class)
    private String mnemonicPassword;

    @Parameter(
            names = {OPT_NUM_ADDRESSES_S, OPT_NUM_ADDRESSES_L},
            description = "Number of addresses to generate",
            converter = IntegerConverter.class,
            validateValueWith = {PositiveIntegerValidator.class, AddressLimitValidator.class})
    private Integer numAddresses = 1;

    @Parameter(
            names = {OPT_HELP_S, OPT_HELP_L},
            description = "Show this usage details page",
            help = true)
    private boolean help;

    //
    // Singleton Setup
    //

    private static Parameters singleton = null;

    private Parameters() {}

    public static Parameters getInstance() {
        if (singleton == null) {
            singleton = new Parameters();
        }

        return singleton;
    }

    //
    // Getters
    //

    public Coin getCoin() {
        return coin;
    }

    public Integer getAccount() {
        return account;
    }

    public Integer getChange() {
        return change;
    }

    public Integer getIndex() {
        return index;
    }

    public boolean isHot() {
        return hot;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public Integer getEntropy() {
        return entropy;
    }

    public String getCustomMnemonic() {
        return customMnemonic;
    }

    public String getMnemonicPassword() {
        return mnemonicPassword;
    }

    public Integer getNumAddresses() {
        return numAddresses;
    }

    public boolean isHelp() {
        return help;
    }
}