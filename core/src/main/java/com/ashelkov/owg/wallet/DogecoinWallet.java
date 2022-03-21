package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.address.BIP44Address;

/**
 * Wallet for storing Dogecoin addresses.
 */
public class DogecoinWallet extends SingleCoinWallet {

    public DogecoinWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.DOGE);
    }
}
