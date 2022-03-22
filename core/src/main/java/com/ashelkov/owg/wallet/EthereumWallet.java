package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.address.BIP44Address;

/**
 * Wallet for storing Ethereum addresses.
 */
public class EthereumWallet extends SingleCoinWallet {

    public EthereumWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.ETH);
    }
}
