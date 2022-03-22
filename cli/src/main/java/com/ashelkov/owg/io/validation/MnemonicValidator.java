package com.ashelkov.owg.io.validation;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import org.web3j.crypto.MnemonicUtils;

/**
 * JCommander validator for mnemonic seed phrase.
 */
public class MnemonicValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (!MnemonicUtils.validateMnemonic(value)) {
            throw new ParameterException("Invalid mnemonic entered");
        }
    }
}
