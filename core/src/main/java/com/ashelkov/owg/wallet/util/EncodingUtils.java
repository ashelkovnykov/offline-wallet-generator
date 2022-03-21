package com.ashelkov.owg.wallet.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.web3j.crypto.Hash;

/**
 * Utilities used to encode addresses in text format.
 */
public class EncodingUtils {

    private static final char[] BITCOIN_ALPHABET =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final char[] XRP_ALPHABET =
            "rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz".toCharArray();
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static final int CHECKSUM_BYTES = 4;

    public static final int MONERO_HOP = 8;
    public static final int MONERO_RES_LONG = 11;
    public static final int MONERO_RES_SHORT = 7;

    private static String doEncodeBase58(byte[] input, char[] alphabet) {
        return doEncodeBase58(input, alphabet, false);
    }

    /**
     * NOTE: This code is mostly copied from:
     *       https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
     *
     * ================================================================================================================
     *
     * Encodes the given bytes as a base58 string (checksum optionally appended).
     *
     * @param rawInput Bytes to encode
     * @param alphabet Ordered alphabet used for encoding
     * @param checksum Append checksum to bytes before encoding if true
     * @return The input bytes as a base58-encoded [[String]]
     */
    private static String doEncodeBase58(byte[] rawInput, char[] alphabet, boolean checksum) {
        if (rawInput.length == 0) {
            return "";
        }

        // Append checksum, if necessary.
        byte[] input;
        if (checksum) {
            input = EncodingUtils.appendChecksum(rawInput);
        } else {
            input = rawInput;
        }

        // Count leading zeros.
        int zeros = 0;
        while (zeros < input.length && input[zeros] == 0) {
            ++zeros;
        }

        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        input = Arrays.copyOf(input, input.length); // since we modify it in-place
        char[] encoded = new char[input.length * 2]; // upper bound
        int outputStart = encoded.length;
        for (int inputStart = zeros; inputStart < input.length; ) {
            encoded[--outputStart] = alphabet[divmod(input, inputStart, 256, 58)];
            if (input[inputStart] == 0) {
                ++inputStart; // optimization - skip leading zeros
            }
        }

        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        char encodedZero = alphabet[0];
        while (outputStart < encoded.length && encoded[outputStart] == encodedZero) {
            ++outputStart;
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = encodedZero;
        }

        // Return encoded string (including encoded leading zeros).
        return new String(encoded, outputStart, encoded.length - outputStart);
    }

    /**
     * NOTE: This code is mostly copied from:
     *       https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
     *
     * ================================================================================================================
     *
     * Divides a number, represented as an array of bytes each containing a single digit in the specified base, by the
     * given divisor. The given number is modified in-place to contain the quotient, and the return value is the
     * remainder.
     *
     * @param number The number to divide
     * @param firstDigit The index within the array of the first non-zero digit (this is used for optimization by
     *                   skipping the leading zeros)
     * @param base The base in which the number's digits are represented (up to 256)
     * @param divisor The number to divide by (up to 256)
     * @return The remainder of the division operation
     */
    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        // this is just long division which accounts for the base of the input digits
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++) {
            int digit = (int) number[i] & 0xFF;
            int temp = remainder * base + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }

    /**
     * Encodes the given bytes as a base58 string using the Bitcoin alphabet (checksum optionally appended).
     *
     * @param input Bytes to encode
     * @param checksum Append checksum to bytes before encoding if true
     * @return The input bytes as a base58-encoded [[String]]
     */
    public static String base58Bitcoin(byte[] input, boolean checksum) {
        return doEncodeBase58(input, BITCOIN_ALPHABET, checksum);
    }

    /**
     * Encodes the given bytes as a base58 string using the Bitcoin alphabet (no checksum is appended).
     *
     * @param input Bytes to encode
     * @return The input bytes as a base58-encoded [[String]]
     */
    public static String base58Bitcoin(byte[] input) {
        return base58Bitcoin(input, false);
    }

    /**
     * Encodes the given bytes as a base58 string using the XRP alphabet (checksum optionally appended).
     *
     * @param input Bytes to encode
     * @param checksum Append checksum to bytes before encoding if true
     * @return The input bytes as a base58-encoded [[String]]
     */
    public static String base58XRP(byte[] input, boolean checksum) {
        return doEncodeBase58(input, XRP_ALPHABET, checksum);
    }

    /**
     * Encodes the given bytes as a base58 string using the XRP alphabet (no checksum is appended).
     *
     * @param input Bytes to encode
     * @return The input bytes as a base58-encoded [[String]]
     */
    public static String base58XRP(byte[] input) {
        return base58XRP(input, false);
    }

    /**
     * Encodes the given bytes as a base58 string using the Monero alphabet (no checksum is appended).
     *
     * Note: Full documentation here: https://monerodocs.org/cryptography/base58/
     *
     * @param input Bytes to encode
     * @return The input bytes as a base58-encoded [[String]]
     */
    public static String base58Monero(byte[] input) {

        StringBuilder result = new StringBuilder();

        // TODO: Need check that input is exactly 65 bytes in length (though the current code should work for any length)
        int length = input.length;
        int hopLength = length - MONERO_HOP;
        int i;

        for (i = 0; i < hopLength; i += MONERO_HOP) {
            byte[] chunk = new byte[MONERO_HOP];
            System.arraycopy(input, i, chunk, 0, MONERO_HOP);
            String chunk58 = base58Bitcoin(chunk);

            result.append(String.valueOf(BITCOIN_ALPHABET[0]).repeat(MONERO_RES_LONG - chunk58.length()));
            result.append(chunk58);
        }

        byte[] chunk = new byte[length - i];
        System.arraycopy(input, i, chunk, 0, length - i);
        String chunk58 = base58Bitcoin(chunk);

        result.append(String.valueOf(BITCOIN_ALPHABET[0]).repeat(MONERO_RES_SHORT - chunk58.length()));
        result.append(chunk58);

        return result.toString();
    }

    /**
     * Append a checksum to then end of a byte array (the right-most SHA256 bytes are used for the checksum).
     *
     * @param input Bytes to which to append checksum
     * @return The input bytes with a SHA256 checksum appended
     */
    public static byte[] appendChecksum(byte[] input) {
        byte[] result = new byte[input.length + CHECKSUM_BYTES];
        byte[] checksum = Hash.sha256(input);
        System.arraycopy(input, 0, result, 0, input.length);
        System.arraycopy(checksum, (32 - CHECKSUM_BYTES), result, input.length, CHECKSUM_BYTES);

        return result;
    }

    /**
     * Convert an input array of regular bytes to 5-bit bytes for use with a base-32 encoding algorithm.
     *
     * Clearly, this algorithm works best when the input length is a multiple of 5, as a byte is 8 bits in length and
     * the lowest common multiple of 5 and 8 is 40. This algorithm is "safe", in that it will pad the input with 0 bytes
     * if the length is not a multiple of 5.
     *
     * @param input Bytes to encode
     * @return The input byte array converted to a longer array of 5-bit bytes
     */
    public static byte[] to5BitBytesSafe(byte[] input) {

        byte[] rawBytes;
        byte[] result;
        int val = input.length % 5;

        if (val == 0) {
            rawBytes = new byte[input.length];
        } else {
            rawBytes = new byte[input.length + (5 - val)];
        }
        result = new byte[(rawBytes.length / 5) * 8];

        System.arraycopy(input, 0, rawBytes, 0, input.length);

        for (int i = 0, j = 0; i < rawBytes.length; i += 5, j += 8) {
            result[j] = (byte)((rawBytes[i] >> 3) & 0x1f);
            result[j + 1] = (byte)(((rawBytes[i] & 0x07) << 2) + ((rawBytes[i + 1] >> 6) & 0x03));
            result[j + 2] = (byte)(((rawBytes[i + 1] & 0x3e) >> 1) & 0x1f);
            result[j + 3] = (byte)(((rawBytes[i + 1] & 0x01) << 4) + ((rawBytes[i + 2] >> 4) & 0x0f));
            result[j + 4] = (byte)(((rawBytes[i + 2] & 0x0f) << 1) + ((rawBytes[i + 3] >> 7) & 0x01));
            result[j + 5] = (byte)(((rawBytes[i + 3] & 0x7c) >> 2) & 0x1f);
            result[j + 6] = (byte)(((rawBytes[i + 3] & 0x03) << 3) + ((rawBytes[i + 4] >> 5) & 0x07));
            result[j + 7] = (byte)(rawBytes[i + 4] & 0x1f);
        }

        return result;
    }

    /**
     * NOTE: Code taken from the top answer to this question:
     *       https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
     *
     * ================================================================================================================
     *
     * Convert bytes to a [[String]] of their hexadecimal values.
     *
     * @param bytes Bytes to encode
     * @return String of hexadecimal characters
     */
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        // TODO: Move to test
//        byte[] test1 = new byte[] {(byte)0xf8, (byte)0x3e, (byte)0x0f, (byte)0x83, (byte)0xe0};
//        logger.info(Arrays.toString(EncodingUtils.to5BitBytesSafe(test1)));
//        byte[] test2 = new byte[] {(byte)0x07, (byte)0xc1, (byte)0xf0, (byte)0x7c, (byte)0x1f};
//        logger.info(Arrays.toString(EncodingUtils.to5BitBytesSafe(test2)));

        return new String(hexChars, StandardCharsets.UTF_8);
    }

    /**
     * Convert an integer to a 4-byte array (little or big endian).
     *
     * @param i Input integer
     * @param isLittleEndian If true, interpret input as little-endian, otherwise interpret as big-endian
     * @return Input integer as array of 4 bytes
     */
    public static byte[] intToFourBytes(int i, boolean isLittleEndian) {
        byte[] result = new byte[4];

        if (isLittleEndian) {
            for (int j = 0; j < 4; j++) {
                result[j] = (byte)(i >> (j * 8));
            }
        } else {
            for (int j = 0; j < 4; j++) {
                result[3 - j] = (byte)(i >> (j * 8));
            }
        }

        return result;
    }

    private EncodingUtils() {}
}
