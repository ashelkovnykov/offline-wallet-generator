package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Stellar addresses.
 */
public class StellarWallet extends SingleCoinWallet {

    public StellarWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.XLM);
    }
}
