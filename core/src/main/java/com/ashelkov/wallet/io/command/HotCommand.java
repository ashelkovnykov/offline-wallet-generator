package com.ashelkov.wallet.io.command;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(commandDescription = "Generate a wallet for multiple cryptocurrencies")
final public class HotCommand {

    //
    // Singleton Setup
    //

    private static HotCommand singleton = null;

    private HotCommand() {}

    public static HotCommand getInstance() {
        if (singleton == null) {
            singleton = new HotCommand();
        }

        return singleton;
    }
}