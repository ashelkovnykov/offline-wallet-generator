package com.ashelkov.owg.wallet;

/**
 * Representation of a cryptocurrency "wallet": one or more addresses for one or more cryptocurrencies, all derived from
 * a single random seed.
 */
public abstract class Wallet {

    protected Wallet() {}

    @Override
    public abstract String toString();

    /**
     * Get a unique [[String]] identifier for the current wallet type.
     *
     * @return String identifier
     */
    public abstract String getIdentifier();
}
