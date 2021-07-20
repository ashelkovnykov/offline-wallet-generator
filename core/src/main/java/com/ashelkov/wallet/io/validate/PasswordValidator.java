package com.ashelkov.wallet.io.validate;

import java.io.Console;
import java.lang.IllegalArgumentException;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.ashelkov.wallet.exceptions.NullConsoleException;

public class PasswordValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {

        String confirmationPassword;

        try {
            confirmationPassword = getConfirmationPassword();

        } catch (IllegalArgumentException e)  {
            e.printStackTrace();
            throw new ParameterException("Password confirmation canceled");

        } catch (NullConsoleException e) {
            e.printStackTrace();
            throw new ParameterException("Console not available for password confirmation");
        }

        if(!value.equals(confirmationPassword)) {
            throw new ParameterException("Passphrases do not match");
        }
    }

    /**
     * Prompts user for a confirmation password via console.
     *
     * @return Confirmation password entered by user
     */
    private static String getConfirmationPassword()
            throws IllegalArgumentException, NullConsoleException {

        Console cons = System.console();
        if(cons == null) {
            throw new NullConsoleException("No console available, this method requires interactive mode");
        }

        char[] passwd = cons.readPassword("[%s]", "Confirm mnemonic password:");
        if(passwd == null) {
            throw new IllegalArgumentException("Password confirmation canceled");
        }

        String output = new String(passwd).trim();

        // Flush password from memory
        java.util.Arrays.fill(passwd, ' ');

        return output;
    }
}
