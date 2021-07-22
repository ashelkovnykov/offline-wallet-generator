package com.ashelkov.owg.io.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.io.conversion.CoinConverter;
import com.ashelkov.owg.io.validation.*;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a wallet for a single cryptocurrency")
final public class ColdCommand {

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

    private static final String OPT_NUM_ADDRESSES_L = "--num-addresses";
    private static final String OPT_NUM_ADDRESSES_S = "-n";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_COIN_S, OPT_COIN_L},
            description = "Crypto currency code of coin for which to generate wallet",
            required = true,
            converter = CoinConverter.class)
    private Coin coin;

    @Parameter(
            names = {OPT_ACCOUNT_S, OPT_ACCOUNT_L},
            description = "BIP 44 account field for address",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    private Integer account;

    @Parameter(
            names = {OPT_CHANGE_S, OPT_CHANGE_L},
            description = "BIP 44 change field for address",
            converter = IntegerConverter.class,
            validateValueWith = BinaryIntegerValidator.class)
    private Integer change;

    @Parameter(
            names = {OPT_INDEX_S, OPT_INDEX_L},
            description = "BIP 44 index field for address",
            converter = IntegerConverter.class,
            validateValueWith = PositiveIntegerValidator.class)
    private Integer index;

    @Parameter(
            names = {OPT_NUM_ADDRESSES_S, OPT_NUM_ADDRESSES_L},
            description = "Number of addresses to generate",
            converter = IntegerConverter.class,
            validateValueWith = {PositiveIntegerValidator.class, AddressLimitValidator.class})
    private Integer numAddresses = 1;

    //
    // Singleton Setup
    //

    private static ColdCommand singleton = null;

    private ColdCommand() {}

    public static ColdCommand getInstance() {
        if (singleton == null) {
            singleton = new ColdCommand();
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

    public Integer getNumAddresses() {
        return numAddresses;
    }
}