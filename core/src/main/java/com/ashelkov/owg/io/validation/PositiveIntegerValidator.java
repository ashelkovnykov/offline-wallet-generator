package com.ashelkov.owg.io.validation;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class PositiveIntegerValidator implements IValueValidator<Integer> {

    @Override
    public void validate(String name, Integer value) throws ParameterException {
        if (value < 0) {
            throw new ParameterException(
                    String.format("Invalid input '%d' to '%s'; value must be positive", value, name));
        }
    }
}
