package com.ashelkov.owg.io.storage;

import com.ashelkov.owg.wallet.Wallet;

public class SecureConsoleWriter extends Writer {

    public SecureConsoleWriter() {}

    public void saveWallet(String mnemonic, Wallet wallet) {
        System.out.println(wallet.toString());
    }
}
