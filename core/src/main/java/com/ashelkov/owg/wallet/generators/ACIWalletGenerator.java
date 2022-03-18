package com.ashelkov.owg.wallet.generators;

import com.ashelkov.owg.wallet.BitcoinWallet;
import com.ashelkov.owg.wallet.SingleCoinWallet;

public abstract class ACIWalletGenerator extends WalletGenerator {

    private static final int PATH_LENGTH = 3;

    public ACIWalletGenerator(boolean genPrivKey, boolean genPubKey) {
        super(genPrivKey, genPubKey);
    }

    @Override
    protected SingleCoinWallet generatePathWalletLogic(int[] path, int numAddresses) {
        return generateWallet(path[0], path[1], path[2], numAddresses);
    }

    @Override
    protected void verifyPartialPath(int[] path)
            throws IllegalArgumentException
    {
        if (path.length != PATH_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(PATH_ERROR_TEMPLATE, PATH_LENGTH, path.length));
        }
    }

    public abstract SingleCoinWallet generateWallet(int account, int change, int index, int numAddresses);
}
