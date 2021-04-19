package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public abstract class Bip44Address {

    protected final Coin coin;
    protected final String address;
    protected final String path;

    protected Bip44Address(Coin coin, String address, String path) {
        this.coin = coin;
        this.address = address;
        this.path = path;
    }

    public String getCoin() {
        return coin.toString();
    }

    public  String getAddress() {
        return address;
    }

    public String getPath() {
        return path;
    }

}
