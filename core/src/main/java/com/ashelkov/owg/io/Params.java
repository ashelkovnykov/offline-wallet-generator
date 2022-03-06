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
import com.ashelkov.owg.io.conversion.OutputFormatConverter;
import com.ashelkov.owg.io.storage.OutputFormat;
import com.ashelkov.owg.io.storage.Writer;
import com.ashelkov.owg.io.storage.WriterFactory;
import com.ashelkov.owg.io.validation.*;

/**
 *
 */
final public class Params {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_OUTPUT_FORMAT_L = "--format";
    private static final String OPT_OUTPUT_FORMAT_S = "-f";

    private static final String OPT_OUTPUT_FILE_L = "--output-file";
    private static final String OPT_OUTPUT_FILE_S = "-o";

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
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_OUTPUT_FORMAT_S, OPT_OUTPUT_FORMAT_L},
            description = "Generated wallet output format",
            converter = OutputFormatConverter.class)
    private OutputFormat outputFormat = DEFAULT_OUTPUT_TYPE;

    @Parameter(
            names = {OPT_OUTPUT_FILE_S, OPT_OUTPUT_FILE_L},
            description = "Directory or path for output files",
            converter = PathConverter.class)
    private Path outputPath = DEFAULT_OUTPUT_DIR;

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
    // CLI Commands
    //

    private static SoloCommand soloCommand = SoloCommand.getInstance();
    private static MultiCommand multiCommand = MultiCommand.getInstance();

    public static final String COMMAND_SOLO = "solo";
    public static final String COMMAND_MULTI = "multi";

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
                    .addCommand(COMMAND_SOLO, soloCommand)
                    .addCommand(COMMAND_MULTI, multiCommand)
                    .build();
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
        return WriterFactory.buildWriter(outputFormat, outputPath, overwrite);
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

    public boolean isHelp() {
        return help;
    }

    public String getCommand() {
        return commander.getParsedCommand();
    }

    public Coin getCoin() {
        return soloCommand.getCoin();
    }

    public Integer getAccount() {
        return soloCommand.getAccount();
    }

    public Integer getChange() {
        return soloCommand.getChange();
    }

    public Integer getIndex() {
        return soloCommand.getIndex();
    }

    public Integer getNumAddresses() {
        return soloCommand.getNumAddresses();
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