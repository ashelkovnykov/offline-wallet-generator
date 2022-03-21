package com.ashelkov.owg.coin.avax;

/**
 * Enumeration of core Avalanche chains, each of which may have an address in an [[AvalancheAddress]] object (see
 * https://docs.avax.network/learn/platform-overview/#exchange-chain-x-chain).
 */
public enum Chain {
    PLATFORM,
    EXCHANGE,
    CONTRACT
}
