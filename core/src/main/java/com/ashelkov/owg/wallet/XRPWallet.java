package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing XRP addresses.
 */
public class XRPWallet extends SingleCoinWallet {

    public XRPWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.XRP);
    }
}