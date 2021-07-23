package com.ashelkov.owg.wallet.generators;

import com.ashelkov.owg.bip.Coin;

public class WalletGeneratorFactory {

    private WalletGeneratorFactory() {}

    public static WalletGenerator getGenerator(byte[] seed, Coin coin) {

        return switch (coin) {
            case BTC -> new BitcoinWalletGenerator(seed);
            case LTC -> new LitecoinWalletGenerator(seed);
            case DOGE -> new DogecoinWalletGenerator(seed);
            case ETH -> new EthereumWalletGenerator(seed);
            case XMR -> new MoneroWalletGenerator(seed);
            case XRP -> new XRPWalletGenerator(seed);
            case XLM -> new StellarWalletGenerator(seed);
            case ALGO -> new AlgorandWalletGenerator(seed);
            case AVAX -> new AvalancheWalletGenerator(seed);
        };
    }
}
