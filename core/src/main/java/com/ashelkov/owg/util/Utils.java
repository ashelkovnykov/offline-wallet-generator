package com.ashelkov.owg.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.LinuxSecureRandom;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private Utils() {}

    public static SecureRandom getSecureRandomInstance() {

        // Preferred method for secure RNG on UNIX systems
        if (SystemUtils.IS_OS_UNIX) {
            new LinuxSecureRandom();
        } else {
            // Attempt to use the strongest possible RNG available
            try {
                return SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                logger.warn(e.getMessage());
            }
        }

        // Default to basic PRNG
        return new SecureRandom();
    }
}
