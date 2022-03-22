package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Algorand addresses.
 */
public class AlgorandWallet extends SingleCoinWallet {

    public AlgorandWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.ALGO);
    }
}
