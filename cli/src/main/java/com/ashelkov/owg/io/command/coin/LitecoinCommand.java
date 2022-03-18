package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Litecoin wallet")
final public class LitecoinCommand extends ACICoinSubCommand {

    //
    // Singleton Setup
    //

    private static LitecoinCommand singleton = null;

    private LitecoinCommand() {}

    public static LitecoinCommand getInstance() {
        if (singleton == null) {
            singleton = new LitecoinCommand();
        }

        return singleton;
    }
}