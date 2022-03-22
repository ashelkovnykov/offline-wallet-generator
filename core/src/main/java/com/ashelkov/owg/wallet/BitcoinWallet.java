package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Bitcoin addresses.
 */
public final class BitcoinWallet extends XPubWallet {

    public BitcoinWallet(BIP44Address xpub, List<BIP44Address> addresses) {
        super(xpub, addresses, Coin.BTC);
    }
}
