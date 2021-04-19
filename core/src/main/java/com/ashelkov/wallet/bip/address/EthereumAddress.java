package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class EthereumAddress extends Bip44Address {

    public EthereumAddress(String address, String path) {
        super(Coin.ETH, address, path);
    }
}
