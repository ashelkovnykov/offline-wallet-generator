package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Ergo addresses.
 */
public class ErgoWallet extends SingleCoinWallet {

    public ErgoWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.ERG);
    }
}
