package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Wallet for storing Avalanche addresses.
 */
public class AvalancheWallet extends SingleCoinWallet {

    public AvalancheWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.AVAX);
    }
}
