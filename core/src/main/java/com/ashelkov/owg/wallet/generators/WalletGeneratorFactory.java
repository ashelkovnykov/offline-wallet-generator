package com.ashelkov.owg.wallet.generators;

import com.ashelkov.owg.bip.Coin;

public class WalletGeneratorFactory {

    private WalletGeneratorFactory() {}

    public static WalletGenerator getGenerator(Coin coin, byte[] seed, boolean genPrivKey, boolean genPubKey) {

        return switch (coin) {
            case BTC -> new BitcoinWalletGenerator(seed, genPrivKey, genPubKey);
            case LTC -> new LitecoinWalletGenerator(seed, genPrivKey, genPubKey);
            case DOGE -> new DogecoinWalletGenerator(seed, genPrivKey, genPubKey);
            case ETH -> new EthereumWalletGenerator(seed, genPrivKey, genPubKey);
            case XMR -> new MoneroWalletGenerator(seed, genPrivKey, genPubKey);
            case XRP -> new XRPWalletGenerator(seed, genPrivKey, genPubKey);
            case XLM -> new StellarWalletGenerator(seed, genPrivKey, genPubKey);
            case ALGO -> new AlgorandWalletGenerator(seed, genPrivKey, genPubKey);
            case AVAX -> new AvalancheWalletGenerator(seed, genPrivKey, genPubKey);
        };
    }
}
