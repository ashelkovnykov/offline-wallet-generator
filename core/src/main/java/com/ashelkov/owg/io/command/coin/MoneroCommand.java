package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.io.validation.PositiveIntegerValidator;
import com.ashelkov.owg.wallet.generators.WalletGenerator;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Monero wallet")
final public class MoneroCommand extends CoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_SUB_ADDRESS_ACCOUNT_L = "--sub-addr-acc";
    private static final String OPT_SUB_ADDRESS_ACCOUNT_S = "-a";

    private static final String OPT_SUB_ADDRESS_INDEX_L = "--sub-addr-index";
    private static final String OPT_SUB_ADDRESS_INDEX_S = "-i";

    private static final String OPT_SPEND_KEY_L = "--spend-key";
    private static final String OPT_SPEND_KEY_S = "-s";

    private static final String OPT_VIEW_KEY_L = "--view-key";
    private static final String OPT_VIEW_KEY_S = "-v";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_SUB_ADDRESS_ACCOUNT_S, OPT_SUB_ADDRESS_ACCOUNT_L},
            description = "Monero subaddress account",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    protected int account = WalletGenerator.DEFAULT_FIELD_VAL;

    @Parameter(
            names = {OPT_SUB_ADDRESS_INDEX_S, OPT_SUB_ADDRESS_INDEX_L},
            description = "Monero subaddress index",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    protected int index = WalletGenerator.DEFAULT_FIELD_VAL;

    @Parameter(
            names = {OPT_SPEND_KEY_S, OPT_SPEND_KEY_L},
            description = "Output the spend key(s)")
    protected boolean genSpendKey = false;

    @Parameter(
            names = {OPT_VIEW_KEY_S, OPT_VIEW_KEY_L},
            description = "Output the view key(s)")
    protected boolean genViewKey = false;

    //
    // Singleton Setup
    //

    private static MoneroCommand singleton = null;

    protected MoneroCommand() {}

    public static MoneroCommand getInstance() {
        if (singleton == null) {
            singleton = new MoneroCommand();
        }

        return singleton;
    }

    //
    // Getters
    //

    public int[] getBipPath() {
        return new int[] {account, index};
    }

    public boolean isGenSpendKey() {
        return genSpendKey;
    }

    public boolean isGenViewKey() {
        return genViewKey;
    }
}
