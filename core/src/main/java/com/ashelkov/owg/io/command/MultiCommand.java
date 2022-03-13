package com.ashelkov.owg.io.command;

import com.beust.jcommander.Parameters;

/**
 *
 */
@Parameters(commandDescription = "Generate a wallet for multiple cryptocurrencies")
final public class MultiCommand {

    public static final String NAME = "multi";

    //
    // Singleton Setup
    //

    private static MultiCommand singleton = null;

    private MultiCommand() {}

    public static MultiCommand getInstance() {
        if (singleton == null) {
            singleton = new MultiCommand();
        }

        return singleton;
    }
}