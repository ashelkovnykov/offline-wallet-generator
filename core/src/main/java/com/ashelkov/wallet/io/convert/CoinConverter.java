package com.ashelkov.wallet.io.convert;

import com.beust.jcommander.converters.EnumConverter;

import com.ashelkov.wallet.bip.Coin;

public class CoinConverter extends EnumConverter<Coin> {
    public CoinConverter(String optionName, Class<Coin> clazz) {
        super(optionName, clazz);
    }
}