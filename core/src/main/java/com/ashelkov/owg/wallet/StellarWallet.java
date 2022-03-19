package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.address.BIP44Address;

import static com.ashelkov.owg.bip.Constants.BIP44_PURPOSE;

public class StellarWallet extends SingleCoinWallet {

    public static final int PURPOSE = BIP44_PURPOSE;

    public StellarWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.XLM);
    }
}
