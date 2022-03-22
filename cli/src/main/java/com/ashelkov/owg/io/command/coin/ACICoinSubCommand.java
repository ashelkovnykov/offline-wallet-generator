package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.io.validation.BinaryIntegerValidator;
import com.ashelkov.owg.io.validation.PositiveIntegerValidator;
import com.ashelkov.owg.wallet.generators.SingleCoinWalletGenerator;

/**
 * Specialization of [[CoinSubCommand]] for coins which make use of the full BIP-44 path.
 */
public abstract class ACICoinSubCommand extends CoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_ACCOUNT_L = "--account";
    private static final String OPT_ACCOUNT_S = "-a";

    private static final String OPT_CHANGE_L = "--change";
    private static final String OPT_CHANGE_S = "-c";

    private static final String OPT_INDEX_L = "--index";
    private static final String OPT_INDEX_S = "-i";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_ACCOUNT_S, OPT_ACCOUNT_L},
            description = "BIP 44 account field for address",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    protected int account = SingleCoinWalletGenerator.DEFAULT_FIELD_VAL;

    @Parameter(
            names = {OPT_CHANGE_S, OPT_CHANGE_L},
            description = "BIP 44 change field for address",
            converter = IntegerConverter.class,
            validateValueWith = BinaryIntegerValidator.class)
    protected int change = SingleCoinWalletGenerator.DEFAULT_FIELD_VAL;

    @Parameter(
            names = {OPT_INDEX_S, OPT_INDEX_L},
            description = "BIP 44 index field for address",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    protected int index = SingleCoinWalletGenerator.DEFAULT_FIELD_VAL;

    //
    // Getters
    //

    public int[] getBipPath() {
        return new int[] {account, change, index};
    }
}
