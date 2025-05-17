package com.ashelkov.owg.io;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.PathConverter;
import org.apache.commons.lang3.SystemUtils;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.io.command.SoloCommand;
import com.ashelkov.owg.io.command.MultiCommand;
import com.ashelkov.owg.io.command.coin.*;
import com.ashelkov.owg.io.output.OutputFormat;
import com.ashelkov.owg.io.output.Writer;
import com.ashelkov.owg.io.output.WriterFactory;
import com.ashelkov.owg.io.util.CommandUtils;
import com.ashelkov.owg.io.validation.*;

/**
 * JCommander parameter specification for CLI.
 */
final public class Params {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_OUTPUT_FORMAT_L = "--format";
    private static final String OPT_OUTPUT_FORMAT_S = "-f";

    private static final String OPT_OUTPUT_FILE_L = "--output-file";
    private static final String OPT_OUTPUT_FILE_S = "-o";

    private static final String OPT_OUTPUT_FILENAME_L = "--output-filename";
    private static final String OPT_OUTPUT_FILENAME_S = "-F";

    private static final String OPT_OVERWRITE_L = "--overwrite";
    private static final String OPT_OVERWRITE_S = "-w";

    private static final String OPT_PRIV_KEY_L = "--priv";
    private static final String OPT_PRIV_KEY_S = "-K";

    private static final String OPT_PUB_KEY_L = "--pub";
    private static final String OPT_PUB_KEY_S = "-k";

    private static final String OPT_ENTROPY_L = "--entropy";
    private static final String OPT_ENTROPY_S = "-e";

    private static final String OPT_MNEMONIC_L = "--custom-mnemonic";
    private static final String OPT_MNEMONIC_S = "-m";

    private static final String OPT_MNEMONIC_PASSWORD_L = "--mnemonic-password";
    private static final String OPT_MNEMONIC_PASSWORD_S = "-p";

    private static final String OPT_HELP_L = "--help";
    private static final String OPT_HELP_S = "-h";

    //
    // CLI Parameter Defaults
    //

    private static final OutputFormat DEFAULT_OUTPUT_TYPE = OutputFormat.WALLET;
    private static final Path DEFAULT_OUTPUT_DIR = Paths.get(getDefaultWalletDir());
    private static final Integer DEFAULT_ENTROPY = 256;

    //
    // CLI Commands
    //

    // Main commands
    private static final SoloCommand SOLO_COMMAND = SoloCommand.getInstance();
    private static final MultiCommand MULTI_COMMAND = MultiCommand.getInstance();

    // Solo subcommands
    private static final BitcoinCommand BITCOIN_COMMAND = BitcoinCommand.getInstance();
    private static final LitecoinCommand LITECOIN_COMMAND = LitecoinCommand.getInstance();
    private static final DogecoinCommand DOGECOIN_COMMAND = DogecoinCommand.getInstance();
    private static final EthereumCommand ETHEREUM_COMMAND = EthereumCommand.getInstance();
    private static final MoneroCommand MONERO_COMMAND = MoneroCommand.getInstance();
    private static final XRPCommand XRP_COMMAND = XRPCommand.getInstance();
    private static final StellarCommand STELLAR_COMMAND = StellarCommand.getInstance();
    private static final AlgorandCommand ALGORAND_COMMAND = AlgorandCommand.getInstance();
    private static final ErgoCommand ERGO_COMMAND = ErgoCommand.getInstance();
    private static final HandshakeCommand HANDSHAKE_COMMAND = HandshakeCommand.getInstance();
    private static final AvalancheCommand AVALANCHE_COMMAND = AvalancheCommand.getInstance();

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_OUTPUT_FORMAT_S, OPT_OUTPUT_FORMAT_L},
            description = "Generated wallet output format")
    private OutputFormat outputFormat = DEFAULT_OUTPUT_TYPE;

    @Parameter(
            names = {OPT_OUTPUT_FILE_S, OPT_OUTPUT_FILE_L},
            description = "Path for output files (directory path or complete filepath with extension)",
            converter = PathConverter.class)
    private Path outputPath = DEFAULT_OUTPUT_DIR;

    @Parameter(
            names = {OPT_OUTPUT_FILENAME_S, OPT_OUTPUT_FILENAME_L},
            description = "Custom filename for the output file (without extension)")
    private String outputFilename;

    @Parameter(
            names = {OPT_OVERWRITE_S, OPT_OVERWRITE_L},
            description = "Overwrite wallet if already exists")
    private boolean overwrite = false;

    @Parameter(
            names = {OPT_PRIV_KEY_S, OPT_PRIV_KEY_L},
            description = "Output the private keys for each generated address")
    private boolean genPrivKey = false;

    @Parameter(
            names = {OPT_PUB_KEY_S, OPT_PUB_KEY_L},
            description = "Output the public keys for each generated address")
    private boolean genPubKey = false;

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
            names = {OPT_HELP_S, OPT_HELP_L},
            description = "Show this usage details page",
            help = true)
    private boolean help;

    //
    // Singleton Setup
    //

    private static Params singleton = null;
    private static JCommander commander = null;

    private Params() {}

    public static Params getInstance() {
        if (singleton == null) {
            singleton = new Params();
            commander = JCommander
                    .newBuilder()
                    .addObject(singleton)
                    .addCommand(SoloCommand.NAME, SOLO_COMMAND)
                    .addCommand(MultiCommand.NAME, MULTI_COMMAND)
                    .build();

            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.BTC.name(), BITCOIN_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.LTC.name(), LITECOIN_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.DOGE.name(), DOGECOIN_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.ETH.name(), ETHEREUM_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.XMR.name(), MONERO_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.XRP.name(), XRP_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.XLM.name(), STELLAR_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.ALGO.name(), ALGORAND_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.ERG.name(), ERGO_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.HNS.name(), HANDSHAKE_COMMAND);
            CommandUtils.addSubCommand(commander, SoloCommand.NAME, Coin.AVAX.name(), AVALANCHE_COMMAND);
        }

        return singleton;
    }

    //
    // Helpers
    //

    private static String getDefaultWalletDir() {

        String root;
        String dir;

        if (SystemUtils.IS_OS_MAC_OSX) {
            root = System.getProperty("user.home");
            dir = String.format("Library%sWallets", File.separator);
        } else if (SystemUtils.IS_OS_WINDOWS) {
            root = System.getenv("APPDATA");
            dir = "Wallets";
        } else {
            root = System.getProperty("user.home");
            dir = ".wallets";
        }

        return String.join(File.separator, root, dir);
    }

    //
    // Getters
    //

    public Writer getOutputWriter() {
        return WriterFactory.buildWriter(outputFormat, outputPath, overwrite, outputFilename);
    }

    public boolean isGenPrivKey() {
        return genPrivKey;
    }

    public boolean isGenPubKey() {
        return genPubKey;
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

    public String getOutputFilename() {
        return outputFilename;
    }

    public boolean isHelp() {
        return help;
    }

    public String getCommand() {
        return commander.getParsedCommand();
    }

    public Coin getCoin() {
        return Coin.valueOf(commander.findCommandByAlias(SoloCommand.NAME).getParsedCommand());
    }

    public Integer getNumAddresses() {
        return SOLO_COMMAND.getNumAddresses();
    }

    public int[] getBipPath() {
        return switch (getCoin()) {
            case BTC -> BITCOIN_COMMAND.getBipPath();
            case LTC -> LITECOIN_COMMAND.getBipPath();
            case DOGE -> DOGECOIN_COMMAND.getBipPath();
            case ETH -> ETHEREUM_COMMAND.getBipPath();
            case XMR -> MONERO_COMMAND.getBipPath();
            case XRP -> XRP_COMMAND.getBipPath();
            case XLM -> STELLAR_COMMAND.getBipPath();
            case ALGO -> ALGORAND_COMMAND.getBipPath();
            case ERG -> ERGO_COMMAND.getBipPath();
            case HNS -> HANDSHAKE_COMMAND.getBipPath();
            case AVAX -> AVALANCHE_COMMAND.getBipPath();
        };
    }

    //
    // Methods
    //

    public void parseArguments(String[] args) {
        commander.parse(args);
    }

    public void printUsage() {
        commander.usage();
    }
}