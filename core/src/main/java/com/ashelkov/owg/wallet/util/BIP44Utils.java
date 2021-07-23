package com.ashelkov.owg.wallet.util;

import com.ashelkov.owg.bip.Constants;

public class BIP44Utils {

    public static String convertPathToText(int[] path) {

        StringBuilder result = new StringBuilder();

        result.append("m/");

        for (int chainVal : path) {
            // Hardened
            if ((chainVal & Constants.HARDENED) != 0) {
                int val = chainVal ^ Constants.HARDENED;

                result.append(val);
                result.append('\'');

            // Not hardened
            } else {
                result.append(chainVal);
            }

            result.append('/');
        }

        result.setLength(result.length() - 1);

        return result.toString();
    }

    private BIP44Utils() {}
}
