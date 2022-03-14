package com.ashelkov.owg.address;

import com.ashelkov.owg.wallet.util.BIP44Utils;

public class MoneroAddress extends BIP44Address {

    protected final String privSpendKey;
    protected final String privViewKey;
    protected final String pubSpendKey;
    protected final String pubViewKey;

    public MoneroAddress(String address, int[] path) {
        this(address, path, null, null, null, null);
    }

    public MoneroAddress(
            String address,
            int[] path,
            String privViewKey,
            String privSpendKey,
            String pubViewKey,
            String pubSpendKey)
    {
        super(address, path, null, null);

        this.privSpendKey = privSpendKey;
        this.privViewKey = privViewKey;
        this.pubSpendKey = pubSpendKey;
        this.pubViewKey = pubViewKey;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append('(');
        result.append(BIP44Utils.convertPathToText(path));
        result.append(')');

        result.append('\t');
        result.append(address);

        if (pubViewKey != null) {
            result.append("\nPUB VIEW =\t");
            result.append(pubViewKey);
        }
        if (pubSpendKey != null) {
            result.append("\nPUB SPEND =\t");
            result.append(pubSpendKey);
        }
        if (privViewKey != null) {
            result.append("\nPRIV VIEW =\t");
            result.append(privViewKey);
        }
        if (privSpendKey != null) {
            result.append("\nPRIV SPEND =\t");
            result.append(privSpendKey);
        }

        return result.toString();
    }
}
