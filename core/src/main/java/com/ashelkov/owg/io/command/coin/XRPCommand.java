package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an XRP wallet")
final public class XRPCommand extends AccountCoinSubCommand {

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
}
