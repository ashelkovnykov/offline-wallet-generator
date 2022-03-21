package com.ashelkov.owg.io.storage;

import com.ashelkov.owg.wallet.Wallet;

/**
 * Base class for output methods to save produced wallets.
 */
public abstract class Writer {

    /**
     * Output the given wallet.
     *
     * @param mnemonic Mnemonic phrase used to produce wallet
     * @param wallet Wallet to output
     */
    public abstract void saveWallet(String mnemonic, Wallet wallet);
}
