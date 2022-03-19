package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.address.BIP84Address;
import com.ashelkov.owg.bip.Coin;

import static com.ashelkov.owg.bip.Constants.BIP84_PURPOSE;

public class LitecoinWallet extends XPubWallet {

    public static final int PURPOSE = BIP84_PURPOSE;

    public LitecoinWallet(BIP84Address xpub, List<BIP44Address> addresses) {
        super(xpub, addresses, Coin.LTC);
    }
}
