package com.ashelkov.owg.wallet;

import java.util.List;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.address.BIP44Address;

import static com.ashelkov.owg.bip.Constants.BIP44_PURPOSE;

public class MoneroWallet extends SingleCoinWallet {

    public static final int PURPOSE = BIP44_PURPOSE;

    private final String privateSpendKey;
    private final String privateViewKey;
    private final boolean hasSubaddresses;

    public MoneroWallet(
            List<BIP44Address> derivedAddresses,
            String privateSpendKey,
            String privateViewKey,
            boolean hasSubaddresses)
    {
        super(derivedAddresses, Coin.XMR);
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

        // append private spend/view keys, if present
        if (privateSpendKey != null) {
            result.append("\nPRIV SPEND =\t");
            result.append(privateSpendKey);
        }
        if (privateViewKey != null) {
            result.append("\nPRIV VIEW =\t");
            result.append(privateViewKey);
        }

        // append addresses
        for (BIP44Address address : addresses) {
            result.append("\n\n");
            result.append(address.toString());
        }

        return result.toString();
    }
}
