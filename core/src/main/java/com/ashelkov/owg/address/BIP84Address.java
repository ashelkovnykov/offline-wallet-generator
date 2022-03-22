package com.ashelkov.owg.address;

import com.ashelkov.owg.bip.Constants;

/**
 * Representation of a single [BIP-84](https://github.com/bitcoin/bips/blob/master/bip-0084.mediawiki) style address
 * ([[BIP44Address]] with an updated purpose field).
 */
public class BIP84Address extends BIP44Address {

    public static final int PURPOSE = 84;

    /**
     * @param address Address
     * @param path BIP-84 path from which the address was derived
     */
    public BIP84Address(String address, int[] path) {
        super(address, path, null, null);
    }

    /**
     * @param address Address
     * @param path BIP-84 path from which the address was derived
     * @param privKey Private key used to derive the address
     * @param pubKey Public key used to derive the address
     */
    public BIP84Address(String address, int[] path, String privKey, String pubKey) {
        super(address, path, privKey, pubKey);
    }

    /**
     * Validate that purpose value is acceptable for BIP84 address.
     *
     * @param purpose Purpose value to validate
     * @return true if the input value matches BIP84 purpose, false otherwise
     */
    @Override
    protected boolean isValidPurpose(int purpose) {
        return (purpose == (Constants.HARDENED | PURPOSE));
    }
}
