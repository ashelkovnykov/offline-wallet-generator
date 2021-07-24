package com.ashelkov.owg.wallet;

public abstract class Wallet {

    protected Wallet() {}

    @Override
    public abstract String toString();

    public abstract String identifier();
}
