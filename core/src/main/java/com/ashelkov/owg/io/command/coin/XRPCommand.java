package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an XRP wallet")
final public class XRPCommand extends AccountCoinSubCommand {

    //
    // CLI Parameter Constants
    //

    private static final String OPT_LEGACY_L = "--legacy";
    private static final String OPT_LEGACY_S = "-l";

    //
    // CLI Parameters
    //

    @Parameter(
            names = {OPT_LEGACY_S, OPT_LEGACY_L},
            description = "Use curve secp256k1 instead of ed25519 to generate address")
    protected boolean legacy = false;

    //
    // Singleton Setup
    //

    private static XRPCommand singleton = null;

    protected XRPCommand() {}

    public static XRPCommand getInstance() {
        if (singleton == null) {
            singleton = new XRPCommand();
        }

        return singleton;
    }

    //
    // Getters
    //

    public boolean isLegacy() {
        return legacy;
    }
}
