package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing a Dogecoin wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Dogecoin wallet")
final public class DogecoinCommand extends ACICoinSubCommand {

    //
    // Singleton Setup
    //

    private static DogecoinCommand singleton = null;

    private DogecoinCommand() {}

    public static DogecoinCommand getInstance() {
        if (singleton == null) {
            singleton = new DogecoinCommand();
        }

        return singleton;
    }
}
