package com.ashelkov.owg.address;

import java.util.Arrays;

import com.ashelkov.owg.wallet.util.BIP44Utils;

public class MoneroAddress extends BIP44Address {

    protected final String pubSpendKey;
    protected final String pubViewKey;
    protected final int account;
    protected final int index;

    public MoneroAddress(String address, int[] path) {
        this(address, path, null, null);
    }

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

        // Monero addresses don't publish paths, since their path is always the same
        if (isSubaddress) {
            result.append(String.format("SUBADDRESS(%d, %d)", account, index));
        } else {
            result.append("STANDARD ADDRESS");
        }
        result.append('\t');
        result.append(address);

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
