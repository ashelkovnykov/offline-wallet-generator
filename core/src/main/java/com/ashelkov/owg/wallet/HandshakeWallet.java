package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.bip.Coin;

import static com.ashelkov.owg.bip.Constants.BIP44_PURPOSE;

public class HandshakeWallet extends SingleCoinWallet {

    public static final Coin COIN = Coin.HNS;
    public static final int PURPOSE = BIP44_PURPOSE;

    protected final BIP84Address xpub;

    public HandshakeWallet(BIP84Address xpub, List<BIP44Address> addresses) {
        super(addresses);
        this.xpub = xpub;
    }


    @Override
    public String getIdentifier() {
        return COIN.toString();
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append coin name
        result.append(COIN);
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