package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.address.BIP44Address;

import static com.ashelkov.owg.bip.Coin.XMR;

/**
 * Wallet for storing Monero addresses.
 */
public class MoneroWallet extends SingleCoinWallet {

    private final String privateSpendKey;
    private final String privateViewKey;
    private final boolean hasSubaddresses;

    public MoneroWallet(
            List<BIP44Address> derivedAddresses,
            String privateSpendKey,
            String privateViewKey,
            boolean hasSubaddresses)
    {
        super(derivedAddresses, XMR);
        this.privateSpendKey = privateSpendKey;
        this.privateViewKey = privateViewKey;
        this.hasSubaddresses = hasSubaddresses;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // warn users of subaddress risks
        if (hasSubaddresses) {
            result.append("WARNING: Monero subaddresses are not fully supported by most Monero wallets.\n");
            result.append("         You should think of them as an experimental convenience feature.\n");
            result.append("         Do your own research and use them at your own risk!\n");
        }

        // append coin name
        result.append(coin);
        result.append(':');

        // append path (all Monero wallets use the exact same path)
        result.append("\n(m/");
        result.append(BIP44Address.PURPOSE);
        result.append("'/");
        result.append(XMR);
        result.append("'/0')");

        // append private spend/view keys, if present
        if (privateSpendKey != null) {
            result.append("\n\tPRIV SPEND\t");
            result.append(privateSpendKey);
        }
        if (privateViewKey != null) {
            result.append("\n\tPRIV VIEW\t");
            result.append(privateViewKey);
        }

        // append addresses
        for (BIP44Address address : addresses) {
            result.append("\n");
            result.append(address.toString());
        }

        return result.toString();
    }
}
