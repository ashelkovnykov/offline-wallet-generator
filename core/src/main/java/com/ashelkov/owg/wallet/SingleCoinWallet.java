package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

public abstract class SingleCoinWallet extends Wallet {

    protected final List<BIP44Address> addresses;
    protected final Coin coin;

    protected SingleCoinWallet(List<BIP44Address> addresses, Coin coin) {

        if (addresses.isEmpty()) {
            throw new IllegalArgumentException(String.format("No addresses in wallet for coin %s", coin.toString()));
        }

        this.addresses = addresses;
        this.coin = coin;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append coin name
        result.append(coin);
        result.append(':');

        // append addresses
        for (BIP44Address address : addresses) {
            result.append('\n');
            result.append(address.toString());
        }

        return result.toString();
    }

    @Override
    public String getIdentifier() {
        return coin.toString();
    }

    public Coin getCoin() {
        return coin;
    }
}
