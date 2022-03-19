package com.ashelkov.owg.wallet;

import java.util.List;

public class MultiCoinWallet extends Wallet {

    private static final String ID = "multi";

    protected final List<SingleCoinWallet> subWallets;

    public MultiCoinWallet(List<SingleCoinWallet> subWallets) {
        if (subWallets.isEmpty()) {
            throw new IllegalArgumentException("No subwallets for multi-coin wallet");
        }

        this.subWallets = subWallets;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        for (SingleCoinWallet wallet : subWallets) {
            result.append("\n\n");
            result.append(wallet.toString());
        }

        result.delete(0, 2);

        return result.toString();
    }
}
