package com.ashelkov.owg.wallet.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.owg.wallet.SingleCoinWallet;

public abstract class WalletGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WalletGenerator.class);
    protected static final String PATH_ERROR_TEMPLATE = "wallet generator path length error; expected %d, got %d";

    public static final int DEFAULT_FIELD_VAL = 0;

    protected final byte[] seed;
    protected final boolean genPrivKey;
    protected final boolean genPubKey;

    public WalletGenerator(byte[] seed, boolean genPrivKey, boolean genPubKey) {

        if (seed.length == 0) {
            throw new IllegalArgumentException("Empty seed");
        }

        this.seed = seed;
        this.genPrivKey = genPrivKey;
        this.genPubKey = genPubKey;
    }

    protected abstract SingleCoinWallet generatePathWalletLogic(int[] path, int numAddresses);

    protected abstract void verifyPartialPath(int[] path)
            throws IllegalArgumentException;

    public abstract SingleCoinWallet generateDefaultWallet();

    public SingleCoinWallet generatePathWallet(int[] partialPath, int numAddresses)
            throws IllegalArgumentException
    {
        try {
            verifyPartialPath(partialPath);
            return generatePathWalletLogic(partialPath, numAddresses);
        } catch(IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }
}
