package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Monero wallet")
final public class MoneroCommand extends AccountCoinSubCommand {

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
}
