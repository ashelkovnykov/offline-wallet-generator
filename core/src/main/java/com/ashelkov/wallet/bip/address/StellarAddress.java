package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class StellarAddress extends Bip44Address {

    public StellarAddress(String address, String path) {
        super(Coin.XLM, address, path);
    }
}
