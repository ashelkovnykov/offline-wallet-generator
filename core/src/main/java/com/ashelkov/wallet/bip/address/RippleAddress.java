package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class RippleAddress extends Bip44Address {

    public RippleAddress(String address, String path) {
        super(Coin.XRP, address, path);
    }
}