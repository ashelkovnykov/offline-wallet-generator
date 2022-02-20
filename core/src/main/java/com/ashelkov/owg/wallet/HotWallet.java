package com.ashelkov.owg.wallet;

import java.util.List;

public class HotWallet extends Wallet {

    private static final String ID = "hot";

    protected final List<ColdWallet> subwallets;

    public HotWallet(List<ColdWallet> subwallets) {
        this.subwallets = subwallets;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        for (ColdWallet wallet : subwallets) {
            result.append("\n\n");
            result.append(wallet.toString());
        }

        result.delete(0, 2);

        return result.toString();
    }
}
