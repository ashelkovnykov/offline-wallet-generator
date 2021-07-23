package com.ashelkov.owg.io.conversion;

import com.beust.jcommander.converters.EnumConverter;

import com.ashelkov.owg.bip.Coin;

public class CoinConverter extends EnumConverter<Coin> {
    public CoinConverter(String optionName, Class<Coin> clazz) {
        super(optionName, clazz);
    }
}