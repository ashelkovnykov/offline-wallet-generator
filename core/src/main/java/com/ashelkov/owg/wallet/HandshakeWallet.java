package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.bip.Coin;

import static com.ashelkov.owg.bip.Constants.BIP44_PURPOSE;

public class HandshakeWallet extends XPubWallet {

    public static final int PURPOSE = BIP44_PURPOSE;

    public HandshakeWallet(BIP84Address xpub, List<BIP44Address> addresses) {
        super(xpub, addresses, Coin.HNS);
    }
}
