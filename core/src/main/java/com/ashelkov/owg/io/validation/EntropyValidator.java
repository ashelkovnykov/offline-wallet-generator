package com.ashelkov.owg.io.validation;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class EntropyValidator implements IValueValidator<Integer> {

    private static final int MAX_ENTROPY = 256;
    private static final int MIN_ENTROPY = 128;
    private static final int ENTROPY_UNITS = 32;

    @Override
    public void validate(String name, Integer value) throws ParameterException {
        if (value < MIN_ENTROPY) {
            throw new ParameterException(
                    String.format("'%d' bits of entropy is insufficient (minimum is %d)", value, MIN_ENTROPY));
        } else if (value > MAX_ENTROPY) {
            throw new ParameterException(
                    String.format("'%d' bits of entropy is too much (maximum is %d)", value, MAX_ENTROPY));
        } else if ((value % ENTROPY_UNITS) != 0) {
            throw new ParameterException(
                    String.format("Invalid entropy value '%d' (must be multiple of %d)", value, ENTROPY_UNITS));
        }
    }
}
