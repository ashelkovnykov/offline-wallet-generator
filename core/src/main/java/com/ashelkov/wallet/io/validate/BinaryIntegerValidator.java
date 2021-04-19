package com.ashelkov.wallet.io.validate;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class BinaryIntegerValidator implements IValueValidator<Integer> {

    @Override
    public void validate(String name, Integer value) throws ParameterException {
        if ((value != 0) && (value != 1)) {
            throw new ParameterException(
                    String.format("Invalid input '%d' to '%s'; value must be 0 or 1", value, name));
        }
    }
}
