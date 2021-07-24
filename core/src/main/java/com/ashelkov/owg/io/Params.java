package com.ashelkov.owg.io;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.io.command.ColdCommand;
import com.ashelkov.owg.io.command.HotCommand;
import com.ashelkov.owg.io.conversion.PathConverter;
import com.ashelkov.owg.io.util.FileUtils;
import com.ashelkov.owg.io.validation.*;

/**
 *
 */
final public class Params {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_OUTPUT_LOCATION_L = "--output-location";
    private static final String OPT_OUTPUT_LOCATION_S = "-o";

    private static final String OPT_OVERWRITE_L = "--overwrite";
    private static final String OPT_OVERWRITE_S = "-w";

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

    private static final Path DEFAULT_OUTPUT_DIR = Paths.get(FileUtils.getDefaultWalletDir());
    private static final Integer DEFAULT_ENTROPY = 256;

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_OUTPUT_LOCATION_S, OPT_OUTPUT_LOCATION_L},
            description = "Output directory or path for wallet file",
            converter = PathConverter.class)
    private Path outputPath = DEFAULT_OUTPUT_DIR;

    @Parameter(
            names = {OPT_OVERWRITE_S, OPT_OVERWRITE_L},
            description = "Overwrite wallet if already exists?")
    private boolean overwrite = false;

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

    private static ColdCommand coldCommand = ColdCommand.getInstance();
    private static HotCommand hotCommand = HotCommand.getInstance();

    public static final String COMMAND_COLD = "cold";
    public static final String COMMAND_HOT = "hot";

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
                    .addCommand(COMMAND_COLD, coldCommand)
                    .addCommand(COMMAND_HOT, hotCommand)
                    .build();
        }

        return singleton;
    }

    //
    // Getters
    //

    public Path getOutputPath() {
        return outputPath;
    }

    public boolean hasOverwrite() {
        return overwrite;
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
        return coldCommand.getCoin();
    }

    public Integer getAccount() {
        return coldCommand.getAccount();
    }

    public Integer getChange() {
        return coldCommand.getChange();
    }

    public Integer getIndex() {
        return coldCommand.getIndex();
    }

    public Integer getNumAddresses() {
        return coldCommand.getNumAddresses();
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