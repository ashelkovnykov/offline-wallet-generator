package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.address.BIP44Address;

import static com.ashelkov.owg.bip.Constants.BIP44_PURPOSE;

public class AlgorandWallet extends SingleCoinWallet {

    public static final int PURPOSE = BIP44_PURPOSE;

    public AlgorandWallet(List<BIP44Address> derivedAddresses) {
        super(derivedAddresses, Coin.ALGO);
    }
}
