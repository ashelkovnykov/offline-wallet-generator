package com.ashelkov.owg.wallet.util;

import com.ashelkov.owg.bip.Constants;

/**
 * Utilities for working with [BIP-44](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki) compliant HD
 * wallet paths.
 */
public class BIP44Utils {

    /**
     * Convert a BIP-44 path to [[String]] in common BIP-44 notation.
     *
     * @param path Input path
     * @return Path as String in common BIP-44 notation
     */
    public static String convertPathToText(int[] path) {

        if (path.length == 0) {
            throw new IllegalArgumentException("Cannot convert empty path to text");
        }

        StringBuilder result = new StringBuilder();

        result.append("m/");

        for (int chainVal : path) {
            result.append(chainValToString(chainVal));
            result.append('/');
        }

        result.setLength(result.length() - 1);

        return result.toString();
    }

    /**
     * Convert a BIP-44 path component value to [[String]] in common BIP-44 notation.
     *
     * @param val BIP-44 path component value
     * @return The input value as a [[String]] in common BIP-44 notation
     */
    public static String chainValToString(int val) {
        // Hardened
        if ((val & Constants.HARDENED) != 0) {
            return String.format("%d'", (val ^ Constants.HARDENED));

        // Not hardened
        } else {
            return String.valueOf(val);
        }
    }

    private BIP44Utils() {}
}
