package com.ashelkov.owg.address;

import java.util.Map;

import com.ashelkov.owg.coin.avax.Chain;
import com.ashelkov.owg.wallet.util.BIP44Utils;

public class AvalancheAddress extends BIP44Address {

    protected final Map<Chain, String> addressMap;

    public AvalancheAddress(Map<Chain, String> addressMap, int[] path) {
        this(addressMap, path, null, null);
    }

    public AvalancheAddress(Map<Chain, String> addressMap, int[] path, String privKey, String pubKey) {
        super("", path, privKey, pubKey);
        this.addressMap = addressMap;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append('(');
        result.append(BIP44Utils.convertPathToText(path));
        result.append(')');

        for (Map.Entry<Chain, String> entry : addressMap.entrySet()) {
            result.append('\n');
            result.append(entry.getKey().toString());
            result.append('\t');
            result.append(entry.getValue());
        }

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
