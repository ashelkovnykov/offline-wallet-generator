package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class BitcoinAddress extends Bip44Address {

    public BitcoinAddress(String address, String path) {
        super(Coin.BTC, address, path);
    }
}
