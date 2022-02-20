package com.ashelkov.owg.address;

import com.ashelkov.owg.wallet.util.BIP44Utils;

public class BIP44Address {

    protected final String address;
    protected final String privKey;
    protected final String pubKey;
    protected final int[] path;

    public BIP44Address(String address, int[] path) {
        this(address, path, null, null);
    }

    public BIP44Address(String address, int[] path, String privKey, String pubKey) {
        this.address = address;
        this.privKey = privKey;
        this.pubKey = pubKey;
        this.path = path;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append('(');
        result.append(BIP44Utils.convertPathToText(path));
        result.append(')');

        result.append('\t');
        result.append(address);

        if (privKey != null) {
            result.append("\nPRIV =\t");
            result.append(privKey);
        }
        if (pubKey != null) {
            result.append("\nPUB =\t");
            result.append(pubKey);
        }

        return result.toString();
    }
}
