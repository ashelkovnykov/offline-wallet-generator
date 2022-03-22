package com.ashelkov.owg.bip;

/**
 * Enumeration of supported coins and their generally-accepted [coin codes](
 * https://github.com/satoshilabs/slips/blob/master/slip-0044.md).
 */
public enum Coin {
    BTC(0),
    LTC(2),
    DOGE(3),
    ETH(60),
    XMR(128),
    XRP(144),
    XLM(148),
    ALGO(283),
    ERG(429),
    HNS(5353),
    AVAX(9000);

    private final int code;

    Coin(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
