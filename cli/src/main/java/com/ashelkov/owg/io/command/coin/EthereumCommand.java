package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing an Ethereum wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate an Ethereum wallet")
final public class EthereumCommand extends IndexCoinSubCommand {

    //
    // Singleton Setup
    //

    private static EthereumCommand singleton = null;

    protected EthereumCommand() {}

    public static EthereumCommand getInstance() {
        if (singleton == null) {
            singleton = new EthereumCommand();
        }

        return singleton;
    }
}
