package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.bip.Coin;

public abstract class XPubWallet extends SingleCoinWallet {

    protected final BIP84Address xpub;

    protected XPubWallet(BIP84Address xpub, List<BIP44Address> addresses, Coin coin) {
        super(addresses, coin);
        this.xpub = xpub;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append coin name
        result.append(coin);
        result.append(':');

        // append xpub
        result.append('\n');
        result.append(xpub.toString());

        // append addresses
        for (BIP44Address address : addresses) {
            result.append('\n');
            result.append(address.toString());
        }

        return result.toString();
    }
}
