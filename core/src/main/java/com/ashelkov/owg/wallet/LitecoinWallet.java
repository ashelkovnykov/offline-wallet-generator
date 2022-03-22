package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Litecoin addresses.
 */
public class LitecoinWallet extends XPubWallet {

    public LitecoinWallet(BIP44Address xpub, List<BIP44Address> addresses) {
        super(xpub, addresses, Coin.LTC);
    }
}
