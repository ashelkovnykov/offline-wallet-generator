package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

import static com.ashelkov.owg.bip.Constants.BIP84_PURPOSE;

public class BitcoinWallet extends ColdWallet {

    public static final Coin COIN = Coin.BTC;
    public static final int PURPOSE = BIP84_PURPOSE;

    public BitcoinWallet(List<BIP44Address> addresses) {
        super(addresses);
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append coin name
        result.append(COIN);
        result.append(':');

        // append addresses
        for (BIP44Address address : addresses) {
            result.append('\n');
            result.append(address.toString());
        }

        return result.toString();
    }
}
