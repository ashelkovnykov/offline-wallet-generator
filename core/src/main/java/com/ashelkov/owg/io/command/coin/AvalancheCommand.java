package com.ashelkov.owg.io.command.coin;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.ashelkov.owg.coin.avax.Chain;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an Avalanche wallet")
final public class AvalancheCommand extends IndexCoinSubCommand {

    //
    // Defaults
    //

    private static final List<Chain> DEFAULT_CHAINS = Arrays.asList(Chain.EXCHANGE);

    //
    // CLI Parameter Constants
    //

    private static final String OPT_CHAINS_L = "--chains";
    private static final String OPT_CHAINS_S = "-c";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_CHAINS_S, OPT_CHAINS_L},
            description = "Chains for which to generate address(es)",
            variableArity = true)
    protected List<Chain> chains = DEFAULT_CHAINS;

    //
    // Singleton Setup
    //

    private static AvalancheCommand singleton = null;

    protected AvalancheCommand() {}

    public static AvalancheCommand getInstance() {
        if (singleton == null) {
            singleton = new AvalancheCommand();
        }

        return singleton;
    }

    //
    // Getters
    //

    public List<Chain> getChains() {
        return chains;
    }
}
