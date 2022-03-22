package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing an Algorand wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an Algorand wallet")
final public class AlgorandCommand extends AccountCoinSubCommand {

    //
    // Singleton Setup
    //

    private static AlgorandCommand singleton = null;

    protected AlgorandCommand() {}

    public static AlgorandCommand getInstance() {
        if (singleton == null) {
            singleton = new AlgorandCommand();
        }

        return singleton;
    }
}
