package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;
import com.ashelkov.owg.bip.Coin;

/**
 * Representation of a cryptocurrency wallet for just one coin which uses an extended public key (a
 * [BIP-32}(https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Extended_keys) feature). Extended public keys
 * allow the derivation of all child public keys, meaning that they allow third-parties to verify the balances of all
 * derived addresses.
 */
public abstract class XPubWallet extends SingleCoinWallet {

    protected final BIP44Address xpub;

    protected XPubWallet(BIP44Address xpub, List<BIP44Address> addresses, Coin coin) {
        super(addresses, coin);
        this.xpub = xpub;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append coin name
        result.append(coin);
        result.append(':');

        // append xpub
        result.append('\n');
        result.append(xpub.toString());

        // append addresses
        for (BIP44Address address : addresses) {
            result.append('\n');
            result.append(address.toString());
        }

        return result.toString();
    }
}
