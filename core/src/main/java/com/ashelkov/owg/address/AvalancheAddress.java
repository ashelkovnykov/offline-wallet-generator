package com.ashelkov.owg.address;

import java.util.Map;

import com.ashelkov.owg.coin.avax.Chain;
import com.ashelkov.owg.wallet.util.BIP44Utils;

/**
 * Representation of a single Avalanche address.
 *
 * An Avalanche wallet has addresses for multiple chains, at the very least the Exchange, Contract, and Platform chains
 * which are core to the network.
 */
public class AvalancheAddress extends BIP44Address {

    protected final Map<Chain, String> addressMap;

    /**
     * @param addressMap Map of Avalanche chain to address
     * @param path BIP-44 path from which the address was derived
     */
    public AvalancheAddress(Map<Chain, String> addressMap, int[] path) {
        this(addressMap, path, null, null);
    }

    /**
     * @param addressMap Map of Avalanche chain to address
     * @param path BIP-44 path from which the address was derived
     * @param privKey Private key used to derive the address
     * @param pubKey Public key used to derive the address
     */
    public AvalancheAddress(Map<Chain, String> addressMap, int[] path, String privKey, String pubKey) {
        super("", path, privKey, pubKey);
        this.addressMap = addressMap;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append path
        result.append('(');
        result.append(BIP44Utils.convertPathToText(path));
        result.append(')');

        // append addresses for each chain
        for (Map.Entry<Chain, String> entry : addressMap.entrySet()) {
            result.append('\n');
            result.append(entry.getKey().toString());
            result.append('\t');
            result.append(entry.getValue());
        }

        // append keys, if present
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
