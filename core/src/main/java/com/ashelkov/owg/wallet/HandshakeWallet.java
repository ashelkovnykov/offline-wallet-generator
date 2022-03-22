package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Handshake addresses.
 */
public class HandshakeWallet extends XPubWallet {

    public HandshakeWallet(BIP44Address xpub, List<BIP44Address> addresses) {
        super(xpub, addresses, Coin.HNS);
    }
}
