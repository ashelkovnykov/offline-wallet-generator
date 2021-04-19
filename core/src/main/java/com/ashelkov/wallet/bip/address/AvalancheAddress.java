package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class AvalancheAddress extends Bip44Address {

    public AvalancheAddress(String address, String path) {
        super(Coin.AVAX, address, path);
    }
}
