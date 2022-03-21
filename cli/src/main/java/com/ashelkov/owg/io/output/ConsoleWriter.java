package com.ashelkov.owg.io.output;

import com.ashelkov.owg.wallet.Wallet;

/**
 * Derived [[Writer]] to output wallet to console.
 */
public class ConsoleWriter extends Writer {

    public ConsoleWriter() {}

    /**
     * Print wallet to console.
     *
     * @param mnemonic Mnemonic phrase used to produce wallet
     * @param wallet Wallet to output
     */
    public void saveWallet(String mnemonic, Wallet wallet) {
        System.out.println(mnemonic);
        System.out.println();
        System.out.println(wallet.toString());
    }
}
