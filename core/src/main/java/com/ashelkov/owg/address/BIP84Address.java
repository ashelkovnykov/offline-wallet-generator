package com.ashelkov.owg.address;

public class BIP84Address extends BIP44Address {

    public BIP84Address(String address, int[] path) {
        super(address, path, null, null);
    }

    public BIP84Address(String address, int[] path, String privKey, String pubKey) {
        super(address, path, privKey, pubKey);
    }
}
