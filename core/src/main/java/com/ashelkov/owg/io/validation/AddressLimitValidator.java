package com.ashelkov.owg.io.validation;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import static com.ashelkov.owg.Constants.ADDRESS_LIMIT;

public class AddressLimitValidator implements IValueValidator<Integer> {

    @Override
    public void validate(String name, Integer value) throws ParameterException {
        if (value > ADDRESS_LIMIT) {
            throw new ParameterException(
                    String.format("Invalid number of addresses %d; limit is %d", value, ADDRESS_LIMIT));
        }
    }
}
