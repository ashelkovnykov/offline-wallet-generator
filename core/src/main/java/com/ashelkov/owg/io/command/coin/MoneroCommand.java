package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Monero wallet")
final public class MoneroCommand extends AccountCoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_SPEND_L = "--spend";
    private static final String OPT_SPEND_S = "-s";

    private static final String OPT_VIEW_L = "--view";
    private static final String OPT_VIEW_S = "-v";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_SPEND_S, OPT_SPEND_L},
            description = "Output the spend key(s)")
    protected boolean genSpendKey = false;

    @Parameter(
            names = {OPT_VIEW_S, OPT_VIEW_L},
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

    public boolean isGenSpendKey() {
        return genSpendKey;
    }

    public boolean isGenViewKey() {
        return genViewKey;
    }
}
