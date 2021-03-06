package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing an Ergo wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an Ergo wallet")
final public class ErgoCommand extends IndexCoinSubCommand {

    //
    // Singleton Setup
    //

    private static ErgoCommand singleton = null;

    protected ErgoCommand() {}

    public static ErgoCommand getInstance() {
        if (singleton == null) {
            singleton = new ErgoCommand();
        }

        return singleton;
    }
}
