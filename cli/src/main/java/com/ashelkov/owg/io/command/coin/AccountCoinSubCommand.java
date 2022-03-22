package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.io.validation.PositiveIntegerValidator;
import com.ashelkov.owg.wallet.generators.SingleCoinWalletGenerator;

/**
 * Specialization of [[CoinSubCommand]] for coins which make use of the BIP-44 'account' field.
 */
public abstract class AccountCoinSubCommand extends CoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_ACCOUNT_L = "--account";
    private static final String OPT_ACCOUNT_S = "-a";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_ACCOUNT_S, OPT_ACCOUNT_L},
            description = "BIP 44 account field for address",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    protected int account = SingleCoinWalletGenerator.DEFAULT_FIELD_VAL;

    //
    // Getters
    //

    public int[] getBipPath() {
        return new int[] {account};
    }
}
