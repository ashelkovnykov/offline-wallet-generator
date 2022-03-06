package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;

public abstract class SingleCoinWallet extends Wallet {

    protected final List<BIP44Address> addresses;

    protected SingleCoinWallet(List<BIP44Address> addresses) {
        this.addresses = addresses;
    }
}
