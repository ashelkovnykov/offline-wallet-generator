package com.ashelkov.owg.address;

import com.ashelkov.owg.bip.Constants;
import com.ashelkov.owg.wallet.util.BIP44Utils;

/**
 * Representation of a single [BIP-44](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki) style address.
 * Contains the [BIP-32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki) path, the address, and possibly
 * the private/public keys.
 */
public class BIP44Address {

    public static final int PURPOSE = 44;

    protected final String address;
    protected final String privKey;
    protected final String pubKey;
    protected final int[] path;

    /**
     * @param address Address
     * @param path BIP-44 path from which the address was derived
     */
    public BIP44Address(String address, int[] path) {
        this(address, path, null, null);
    }

    /**
     * @param address Address
     * @param path BIP-44 path from which the address was derived
     * @param privKey Private key used to derive the address
     * @param pubKey Public key used to derive the address
     */
    public BIP44Address(String address, int[] path, String privKey, String pubKey) {
        if (!isValidPurpose(path[0])) {
            String purposeString = BIP44Utils.chainValToString(path[0]);
            throw new IllegalArgumentException(String.format("Bad 'purpose' value for BIP44 path: %s", purposeString));
        }

        this.address = address;
        this.privKey = privKey;
        this.pubKey = pubKey;
        this.path = path;
    }

    /**
     * Validate that purpose value is acceptable for BIP44 address.
     *
     * @param purpose Purpose value to validate
     * @return true if the input value matches BIP44 purpose, false otherwise
     */
    protected boolean isValidPurpose(int purpose) {
        return (purpose == (Constants.HARDENED | PURPOSE));
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        // append BIP-32 path
        result.append('(');
        result.append(BIP44Utils.convertPathToText(path));
        result.append(')');

        // append address
        result.append('\t');
        result.append(address);

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
