package com.ashelkov.owg.io.validation;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

/**
 * JCommander validator for integer with binary value.
 */
public class BinaryIntegerValidator implements IValueValidator<Integer> {

    @Override
    public void validate(String name, Integer value) throws ParameterException {
        if ((value != 0) && (value != 1)) {
            throw new ParameterException(
                    String.format("Invalid input '%d' to '%s'; value must be 0 or 1", value, name));
        }
    }
}
