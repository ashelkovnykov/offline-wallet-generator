package com.ashelkov.owg.io.storage;

import com.ashelkov.owg.wallet.Wallet;

public class ConsoleWriter extends Writer {

    public ConsoleWriter() {}

    public void saveWallet(String mnemonic, Wallet wallet) {

        System.out.println(mnemonic);
        System.out.println();
        System.out.println(wallet.toString());
    }
}
