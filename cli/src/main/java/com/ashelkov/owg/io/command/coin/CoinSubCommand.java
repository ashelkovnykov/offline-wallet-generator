package com.ashelkov.owg.io.command.coin;

/**
 * Sub-command for [[SoloCommand]] JCommander command to select type of coin for which to produce wallet.
 */
public abstract class CoinSubCommand {

    //
    // Getters
    //

    public abstract int[] getBipPath();
}
