package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.io.validation.PositiveIntegerValidator;
import com.ashelkov.owg.wallet.generators.SingleCoinWalletGenerator;

/**
 * Specialization of [[CoinSubCommand]] for coins which make use of the BIP-44 'index' field.
 */
public abstract class IndexCoinSubCommand extends CoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_INDEX_L = "--index";
    private static final String OPT_INDEX_S = "-i";

    //
    // CLI Parameters
    //

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
        return new int[] {index};
    }
}
