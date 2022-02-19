package com.ashelkov.owg.io.storage;

import com.ashelkov.owg.wallet.Wallet;

public abstract class Writer {

    public abstract void saveWallet(String mnemonic, Wallet wallet);

}
