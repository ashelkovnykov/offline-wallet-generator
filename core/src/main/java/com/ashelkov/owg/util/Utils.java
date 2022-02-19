package com.ashelkov.owg.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.LinuxSecureRandom;

public final class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static SecureRandom random;

    private Utils() {}

    private static void initSecureRandom() {

        // Preferred method for secure RNG on UNIX systems
        if (SystemUtils.IS_OS_UNIX) {
            new LinuxSecureRandom();
        } else {
            // Attempt to use the strongest possible RNG available
            try {
                random = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                logger.warn(e.getMessage());
            }
        }

        // Default to basic PRNG
        random = new SecureRandom();
    }

    public static SecureRandom getSecureRandom() {

        if (random == null) {
            initSecureRandom();
        }

        return random;
    }
}
