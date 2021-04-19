package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class AlgorandAddress extends Bip44Address {

    public AlgorandAddress(String address, String path) {
        super(Coin.ALGO, address, path);
    }
}
