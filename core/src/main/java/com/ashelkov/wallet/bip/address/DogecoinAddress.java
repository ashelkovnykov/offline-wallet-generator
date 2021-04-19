package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class DogecoinAddress extends Bip44Address {

    public DogecoinAddress(String address, String path) {
        super(Coin.DOGE, address, path);
    }
}
