package com.ashelkov.owg.wallet.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.owg.wallet.SingleCoinWallet;

/**
 * Base class for factory classes which generate [[SingleCoinWallet]] objects.
 */
public abstract class SingleCoinWalletGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SingleCoinWalletGenerator.class);
    protected static final String PATH_ERROR_TEMPLATE = "wallet generator path length error; expected %d, got %d";

    public static final int DEFAULT_FIELD_VAL = 0;

    protected final byte[] seed;
    protected final boolean genPrivKey;
    protected final boolean genPubKey;

    public SingleCoinWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {

        if (seed.length == 0) {
            throw new IllegalArgumentException("Empty seed");
        }

        this.seed = seed;
        this.genPrivKey = genPrivKey;
        this.genPubKey = genPubKey;
    }

    /**
     * Core logic for generating a [[SingleCoinWallet]] from a partial path.
     *
     * @param partialPath Array of BIP-32 path elements
     * @param numAddresses Number of addresses to generate
     * @return New wallet containing addresses for given path
     */
    protected abstract SingleCoinWallet generatePathWalletLogic(int[] partialPath, int numAddresses);

    /**
     * Verify that a given partial path is valid (throws IllegalArgumentException if invalid).
     *
     * @param partialPath Path to verify
     * @throws IllegalArgumentException
     */
    protected abstract void verifyPartialPath(int[] partialPath);

    /**
     * Generate the default [[SingleCoinWallet]] for a coin ('account', 'change', 'index', etc. fields have default
     * value).
     *
     * @return New wallet containing the single default address
     */
    public abstract SingleCoinWallet generateDefaultWallet();

    /**
     * Generate a [[SingleCoinWallet]] from a partial path (the 'purpose' and 'coin code' are determined by the wallet).
     * Optionally, generate more than one address by incrementing the 'account' or 'index' fields.
     *
     * This function is a wrapper around the actual wallet-generation logic.
     *
     * @param partialPath Array of BIP-32 path elements
     * @param numAddresses Number of addresses to generate
     * @return New wallet containing addresses for given path
     * @throws IllegalArgumentException
     */
    public SingleCoinWallet generatePathWallet(int[] partialPath, int numAddresses)
    {
        verifyPartialPath(partialPath);
        return generatePathWalletLogic(partialPath, numAddresses);
    }
}
