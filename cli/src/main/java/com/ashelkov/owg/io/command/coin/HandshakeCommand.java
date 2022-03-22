package com.ashelkov.owg.io.command.coin;

import com.beust.jcommander.Parameters;

/**
 * Sub-command to [[SoloCommand]] for producing a Handshake wallet.
 */
@Parameters(
        separators = "=",
        commandDescription = "Generate a Handshake wallet")
final public class HandshakeCommand extends ACICoinSubCommand {

    //
    // Singleton Setup
    //

    private static HandshakeCommand singleton = null;

    protected HandshakeCommand() {}

    public static HandshakeCommand getInstance() {
        if (singleton == null) {
            singleton = new HandshakeCommand();
        }

        return singleton;
    }
}