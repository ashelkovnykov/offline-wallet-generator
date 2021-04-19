package com.ashelkov.wallet.bip.wallet;

import com.ashelkov.wallet.bip.Coin;

public class WalletFactory {

    private WalletFactory() {}

    public static Wallet generateWallet(byte[] seed, Coin coin) {

        return switch (coin) {
            case BTC -> new BitcoinWallet(seed);
            case LTC -> new LitecoinWallet(seed);
            case DOGE -> new DogecoinWallet(seed);
            case ETH -> new EthereumWallet(seed);
            case XRP -> new RippleWallet(seed);
            case XLM -> new StellarWallet(seed);
            case ALGO -> new AlgorandWallet(seed);
            case AVAX -> new AvalancheWallet(seed);
        };
    }
}
