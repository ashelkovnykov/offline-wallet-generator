package com.ashelkov.owg.wallet.generators;

import com.ashelkov.owg.wallet.SingleCoinWallet;

/**
 * Specialization of [[SingleCoinWalletGenerator]] for coins which only use the 'account' and 'index' field of a BIP-44
 * path.
 */
public abstract class AccountIndexWalletGenerator extends SingleCoinWalletGenerator {

    private static final int PATH_LENGTH = 2;

    public AccountIndexWalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {
        super(seed, genPrivKey, genPubKey);
    }

    /**
     * Core logic for generating a [[SingleCoinWallet]] from a partial path.
     *
     * @param partialPath Array of BIP-32 path elements
     * @param numAddresses Number of addresses to generate
     * @return New wallet containing addresses for given path
     */
    @Override
    protected SingleCoinWallet generatePathWalletLogic(int[] partialPath, int numAddresses) {
        return generateWallet(partialPath[0], partialPath[1], numAddresses);
    }

    /**
     * Partial path should have exactly two inputs: 'account' value and 'index' value, in that order.
     *
     * @param partialPath Path to verify
     * @throws IllegalArgumentException
     */
    @Override
    protected void verifyPartialPath(int[] partialPath) {
        if (partialPath.length != PATH_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(PATH_ERROR_TEMPLATE, PATH_LENGTH, partialPath.length));
        }
    }

    /**
     * Generate a [[SingleCoinWallet]] for a particular 'account' and 'index' (the 'purpose' and 'coin' fields are
     * determined by the derived class; 'change' field is unused). Optionally, generate more than one address by
     * incrementing the 'index' field.
     *
     * @param account Account value
     * @param index Index value
     * @param numAddresses Number of addresses to generate
     * @return New wallet
     */
    public abstract SingleCoinWallet generateWallet(int account, int index, int numAddresses);
}
