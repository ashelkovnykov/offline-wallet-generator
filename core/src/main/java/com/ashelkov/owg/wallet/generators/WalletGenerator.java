package com.ashelkov.owg.wallet.generators;

import com.ashelkov.owg.bip.Coin;
import com.ashelkov.owg.wallet.ColdWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WalletGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WalletGenerator.class);
    private static final String UNUSED_BIP_FIELD_MSG = "'%s' BIP44 field not used for %s; value '%d' ignored";
    private static final String MISSING_BIP_FIELD_MSG = "'%s' BIP44 field not specified for %s; defaulting to '%d'";

    protected static final String ACCOUNT = "account";
    protected static final String CHANGE = "change";
    protected static final String INDEX = "address index";
    protected static final int DEFAULT_FIELD_VAL = 0;

    protected static void logWarning(String field, Coin coin, int val) {
        logger.warn(String.format(UNUSED_BIP_FIELD_MSG, field, coin, val));
    }

    protected static void logMissing(String field, Coin coin) {
        logger.info(String.format(MISSING_BIP_FIELD_MSG, field, coin, DEFAULT_FIELD_VAL));
    }

    protected boolean genPrivKey;
    protected boolean genPubKey;

    public WalletGenerator(boolean genPrivKey, boolean genPubKey) {
        this.genPrivKey = genPrivKey;
        this.genPubKey = genPubKey;
    }

    protected abstract void logWarning(String field, int val);
    protected abstract void logMissing(String field);

    public abstract ColdWallet generateWallet(
            Integer account,
            Integer change,
            Integer index,
            int numAddresses);

    public abstract ColdWallet generateDefaultWallet();
}
