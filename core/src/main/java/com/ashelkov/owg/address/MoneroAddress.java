package com.ashelkov.owg.address;

import java.util.Arrays;

import com.ashelkov.owg.wallet.util.BIP44Utils;

/**
 * Representation of a single Monero address.
 *
 * A Monero address is a lot like a [[BIP44Address]], except that it doesn't publish the address path (since all Monero
 * addresses use the same path) and it *does* publish whether the address is a standard address or a subaddress (and if
 * so, which subaddress).
 */
public class MoneroAddress extends BIP44Address {

    protected final String pubSpendKey;
    protected final String pubViewKey;
    protected final int account;
    protected final int index;

    /**
     * @param address Address
     * @param path An ordered set of indices used to verify that the address was derived from BIP-44 path m/44'/128'/0'
     *             and which 'account' and 'index' indices were used to compute the address (if it is a subaddress)
     */
    public MoneroAddress(String address, int[] path) {
        this(address, path, null, null);
    }

    /**
     * @param address Address
     * @param path An ordered set of indices used to verify that the address was derived from BIP-44 path m/44'/128'/0'
     *             and which 'account' and 'index' indices were used to compute the address (if it is a subaddress)
     */
    public MoneroAddress(String address, int[] path, String pubSpendKey, String pubViewKey)
    {
        super(address, Arrays.copyOfRange(path, 0, 2), null, null);

        this.pubSpendKey = pubSpendKey;
        this.pubViewKey = pubViewKey;
        this.account = path[2];
        this.index = path[3];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean isSubaddress = (account != 0) || (index != 0);

        // append address type
        if (isSubaddress) {
            result.append(String.format("SUBADDRESS(%d, %d)", account, index));
        } else {
            result.append("STANDARD ADDRESS");
        }
        // append address
        result.append('\t');
        result.append(address);

        // append keys, if present
        if (pubSpendKey != null) {
            result.append("\n\tPUB SPEND\t");
            result.append(pubSpendKey);
        }
        if (pubViewKey != null) {
            result.append("\n\tPUB VIEW\t");
            result.append(pubViewKey);
        }

        return result.toString();
    }
}
