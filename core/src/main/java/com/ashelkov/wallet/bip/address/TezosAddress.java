package com.ashelkov.wallet.bip.address;

import com.ashelkov.wallet.bip.Coin;

public class TezosAddress extends Bip44Address {
  public TezosAddress(String address, String path) {
    super(Coin.XTZ, address, path);
  }  
}
