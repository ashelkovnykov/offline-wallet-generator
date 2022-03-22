package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing a Stellar wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Stellar wallet")
final public class StellarCommand extends AccountCoinSubCommand {

    //
    // Singleton Setup
    //

    private static StellarCommand singleton = null;

    protected StellarCommand() {}

    public static StellarCommand getInstance() {
        if (singleton == null) {
            singleton = new StellarCommand();
        }

        return singleton;
    }
}
