package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class MoneroAddress extends Bip44Address {

    public MoneroAddress(String address, String path) {
        super(Coin.XMR, address, path);
    }
}
