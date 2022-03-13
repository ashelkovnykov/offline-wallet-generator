package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Bitcoin wallet")
final public class BitcoinCommand extends ACICoinSubCommand {

    //
    // Singleton Setup
    //

    private static BitcoinCommand singleton = null;

    protected BitcoinCommand() {}

    public static BitcoinCommand getInstance() {
        if (singleton == null) {
            singleton = new BitcoinCommand();
        }

        return singleton;
    }
}