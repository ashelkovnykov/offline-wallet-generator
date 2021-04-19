package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class LitecoinAddress extends Bip44Address {

    public LitecoinAddress(String address, String path) {
        super(Coin.LTC, address, path);
    }
}
