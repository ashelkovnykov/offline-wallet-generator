package com.ashelkov.wallet.bip;

/**
 * Monero (XMR)
 * Siacoin (SIA)
 */
public enum Coin {

    // Coin chain codes derived from here:
    // https://github.com/satoshilabs/slips/blob/master/slip-0044.md
    BTC(0),
    LTC(2),
    DOGE(3),
    ETH(60),
    XRP(144),
    XLM(148),
    ALGO(283),
    XTZ(1729),
    AVAX(9000);

    private final int code;

    Coin(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
