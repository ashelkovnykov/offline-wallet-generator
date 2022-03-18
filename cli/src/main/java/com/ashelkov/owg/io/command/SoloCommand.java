package com.ashelkov.owg.io.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.IntegerConverter;

import com.ashelkov.owg.io.validation.*;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a wallet for a single cryptocurrency")
final public class SoloCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_NUM_ADDRESSES_L = "--num-addresses";
    private static final String OPT_NUM_ADDRESSES_S = "-n";

    public static final String NAME = "solo";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_NUM_ADDRESSES_S, OPT_NUM_ADDRESSES_L},
            description = "Number of addresses to generate",
            converter = IntegerConverter.class,
            validateValueWith = {PositiveIntegerValidator.class, AddressLimitValidator.class})
    private Integer numAddresses = 1;

    //
    // Singleton Setup
    //

    private static SoloCommand singleton = null;

    private SoloCommand() {}

    public static SoloCommand getInstance() {
        if (singleton == null) {
            singleton = new SoloCommand();
        }

        return singleton;
    }

    //
    // Getters
    //

    public Integer getNumAddresses() {
        return numAddresses;
    }
}