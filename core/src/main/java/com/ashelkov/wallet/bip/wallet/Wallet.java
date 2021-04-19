package com.ashelkov.wallet.bip.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.wallet.bip.Coin;
import com.ashelkov.wallet.bip.address.Bip44Address;

public abstract class Wallet {

    protected final Coin coin;
    protected final String coinName;

    protected Wallet(Coin coin) {
        this.coin = coin;
        this.coinName = coin.toString();
    }

    protected static final Logger logger = LoggerFactory.getLogger(Wallet.class);

    protected static final String UNUSED_BIP_PATH_MSG_BASE = "'%s' BIP44 field not used for %s; value '%d' ignored";
    protected static final String MISSING_BIP_PATH_MSG_BASE = "'%s' BIP44 field not specified for %s; defaulting to '0'";
    protected static final String ACCOUNT = "account";
    protected static final String CHANGE = "change";
    protected static final String INDEX = "address index";

    protected static void logWarning(String path, String coin, int val) {
        logger.warn(String.format(UNUSED_BIP_PATH_MSG_BASE, path, coin, val));
    }

    protected static void logMissing(String path, String coin) {
        logger.info(String.format(MISSING_BIP_PATH_MSG_BASE, path, coin));
    }

    public abstract Bip44Address getSpecificAddress(Integer account, Integer change, Integer addressIndex);

    public abstract Bip44Address getDefaultAddress(int index);

}
