package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an Avalanche wallet")
final public class AvalancheCommand extends IndexCoinSubCommand {

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
}
